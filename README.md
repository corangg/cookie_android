# 너쿠 (NueCoo)

> Jetpack Compose 기반 체험형 쿠키 오픈 Android 앱

<br>

## 프로젝트 소개

사용자가 직접 쿠키를 오픈하는 경험을 중심으로 Jetpack Compose 기반 체험형 UI를 구현했습니다.
기존 프로젝트의 데이터 저장 구조 한계를 분석하고 더 안정적인 방식으로 개선한 1인 사이드 프로젝트입니다.

- **기간**: 2026.01 ~ 진행 중
- **플랫폼**: Android (minSdk 35 / targetSdk 36)
- **구성**: Android App (Kotlin/Compose) + Firebase Cloud Functions (TypeScript)
- **상태**: 배포 준비 중

<br>

## 기술 스택

| 분류 | 기술 |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose, Material3 |
| Architecture | Clean Architecture, MVVM |
| Async | Coroutines, Flow, StateFlow |
| DI | Hilt |
| Local DB | Room |
| Background | WorkManager |
| Firebase | Realtime Database, Cloud Functions (TypeScript) |
| 기타 | DataStore, Glance Widget, Timber |

<br>

## 아키텍처

Clean Architecture + MVVM 기반으로 기능 단위 모듈화를 적용했습니다.

```
com.nuecoo
├── core
│   ├── presentation   # 공통 UI 컴포넌트
│   ├── theme
│   └── util
└── feature
    ├── auth
    │   ├── data
    │   ├── domain
    │   ├── di
    │   └── presentation
    ├── main
    │   ├── data
    │   ├── domain
    │   ├── di
    │   └── presentation
    │       └── oven    # 쿠키 오픈 화면
    ├── splash
    └── widget
```

ViewModel은 Compose와 함께 단방향 데이터 흐름(State ↑ / Event ↓)으로 화면 상태를 관리합니다.

<br>

## 핵심 구현

### 1. 커스텀 핀치 제스처 기반 쿠키 오픈

표준 제스처 API 대신 `PointerEventType`을 직접 다뤄 두 손가락 포인터를 수동 추적하고 `hypot`으로 거리를 계산하는 커스텀 핀치 감지기를 구현했습니다. threshold는 기기 밀도 기반 dp로 환산해 기기 무관하게 동일한 체감을 유지했습니다.

```kotlin
private fun Modifier.cookiePinchOpenDetector(
    enabled: Boolean,
    onOpen: () -> Unit
): Modifier {
    return if (!enabled) this
    else this.pointerInput(Unit) {
        val activePointers = mutableMapOf<Long, Offset>()
        var initialDistance = 0f
        val threshold = with(density) { 80.dp.toPx() } // px 하드코딩 대신 기기 밀도 기반 변환

        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent()
                when (event.type) {
                    PointerEventType.Press -> { /* 포인터 등록 + 초기 거리 계산 */ }
                    PointerEventType.Move -> {
                        val currentDistance = hypot(/* 두 포인터 좌표 계산 */)
                        if (currentDistance - initialDistance > threshold) {
                            onOpen()
                            activePointers.clear()
                        }
                    }
                    PointerEventType.Release -> { /* 포인터 해제 */ }
                }
            }
        }
    }
}
```

**성능 최적화 — 이미지 프리로딩**

애니메이션 시작 전 프레임 이미지를 IO 스레드에서 미리 로딩해 핀치 트리거 시 프레임 전환 버벅임을 방지했습니다.

```kotlin
LaunchedEffect(cookieData.type) {
    withContext(Dispatchers.IO) {
        (animFrames + getOpenedCookieImage(cookieData.type)).forEach { resId ->
            runCatching { ResourcesCompat.getDrawable(context.resources, resId, null) }
        }
    }
}
```

**상태 전환 흐름**

```
triggerAnimation → isAnimating → 프레임 전환 → 햅틱 피드백 → isOpened
```

LaunchedEffect 단일 키(`isAnimating`)로 4단계 상태 전환 순서를 보장해 상태 누락 및 중단 없이 동작합니다.

<br>

### 2. 오프라인 우선 데이터 아키텍처

#### 기존 구조의 문제

```
사용자 액션 → RTDB 저장 → 리스너 감지 → Room 갱신 → UI 반영
```

- Room이 로컬 캐시에 머물러 RTDB가 단일 진실 공급원(SSOT) 역할을 담당
- UI 갱신이 직렬 파이프라인에 종속되어 네트워크 응답 전까지 반응 불가
- 클라이언트 주도 쓰기 방식으로 서버 측 원자적 제어 장치 부재

#### 개선된 구조

```
사용자 액션 ─┬─→ Room 즉시 저장 → UI 즉시 반영 (Room Flow 관찰)
             └─→ WorkManager → RTDB 동기화 (독립 백그라운드 트랙)
                               ↓
                         Cloud Function
                         (idempotency guard + 번호 생성)
                               ↓
                         Room 값 갱신 → UI 자동 반영
```

**핵심 설계 결정 3가지**

| 결정 | 내용 |
|---|---|
| Room SSOT 전환 | 사용자 액션 즉시 로컬 저장, UI 즉각 반응 |
| 서버 권위 원칙 | 쿠키 번호 생성 권한을 Cloud Function으로 이전, 다기기 선노출 구조적 차단 |
| 동시성 제어 | Cloud Functions idempotency guard + RTDB 트랜잭션으로 중복 처리 원천 차단 |

**결과**

- 네트워크 없이도 쿠키 오픈 가능, WorkManager가 재연결 시 자동 동기화
- 서버 응답 대기 없이 로컬 기록 즉시 UI 반영
- eventId 기반 멱등성으로 동일 요청 재시도 시 이중 처리 없음

<br>

## 백엔드 설계 (Firebase Cloud Functions)

| 함수 | 역할 |
|---|---|
| `syncCookieEvent` | 쿠키 이벤트 서버 동기화 + idempotency guard |
| `getCookieTypeCounts` | 쿠키 타입별 수집 통계 집계 |
| `fetchCookieEvents` | 쿠키 이벤트 조회 |
| `sendVerificationCode` | SMS 인증번호 발송 (SOLAPI 연동) |
| `verifyPhoneForSignup` | 휴대폰 번호 인증 및 회원가입 |
| `cleanupAnonymousUsers` | Cloud Scheduler 기반 미인증 계정 자동 정리 |

