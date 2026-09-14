package com.nuecoo.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.architecture.KoArchitectureCreator.assertArchitecture
import com.lemonappdev.konsist.api.architecture.Layer
import org.junit.Test

// ──────────────────────────────────────────────
// core / feature 경계 강제
//
// core 는 어떤 feature 에도 의존하지 않는다 (core.dependsOnNothing()).
// 각 feature 는 core 에는 의존할 수 있지만, 서로에게는 의존할 수 없다.
//
// 주의: Konsist의 dependsOn()은 허용 목록일 뿐 배타적 강제가 아니다.
// (예: featureAuth.dependsOn(core) 라고 해서 featureAuth -> featureMain 의존이
// 자동으로 금지되지 않는다 — 실제로 목킹 없이 확인됨.) 그래서 feature 간 금지는
// doesNotDependOn()으로 모든 쌍을 명시적으로 선언해야 한다.
//
// ⚠️ 새 feature 모듈을 추가하면 아래 Layer 선언과 doesNotDependOn 조합에도
// 반드시 등록할 것 — 여기 등록하지 않은 feature는 검사 대상에서 빠진다.
// ──────────────────────────────────────────────
class ArchitectureTest {

    @Test
    fun `core is independent and feature modules do not depend on each other`() {
        Konsist.scopeFromProduction().assertArchitecture {
            val core = Layer("Core", "com.nuecoo.core..")
            val featureAuth = Layer("FeatureAuth", "com.nuecoo.feature.auth..")
            val featureMain = Layer("FeatureMain", "com.nuecoo.feature.main..")
            val featureSplash = Layer("FeatureSplash", "com.nuecoo.feature.splash..")
            val featureWidget = Layer("FeatureWidget", "com.nuecoo.feature.widget..")

            core.dependsOnNothing()
            featureAuth.dependsOn(core)
            featureMain.dependsOn(core)
            featureSplash.dependsOn(core)
            featureWidget.dependsOn(core)

            // feature 간 상호 의존 금지 (각 feature는 core에만 의존해야 한다)
            listOf(featureMain, featureSplash, featureWidget).doesNotDependOn(featureAuth)
            listOf(featureAuth, featureSplash, featureWidget).doesNotDependOn(featureMain)
            listOf(featureAuth, featureMain, featureWidget).doesNotDependOn(featureSplash)
            listOf(featureAuth, featureMain, featureSplash).doesNotDependOn(featureWidget)
        }
    }
}
