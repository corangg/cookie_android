import { onCall, HttpsError } from "firebase-functions/v2/https";
import { onSchedule } from "firebase-functions/v2/scheduler";
import * as admin from "firebase-admin";
import * as crypto from "crypto";
import { sendSms } from "./solapi";

admin.initializeApp();
const rtdb = admin.database();

const CODE_EXPIRY_SECONDS = 180;
const MAX_ATTEMPTS = 5;
const RESEND_COOLDOWN_SECONDS = 60;

type Purpose = "SIGNUP" | "FIND_EMAIL" | "RESET_PASSWORD";

function hashCode(code: string): string {
  return crypto.createHash("sha256").update(code).digest("hex");
}

function generateCode(): string {
  return Math.floor(100000 + Math.random() * 900000).toString();
}

function encodeKey(value: string): string {
  return encodeURIComponent(value);
}

function toDomesticFormat(e164Phone: string): string {
  return e164Phone.replace(/^\+82/, "0");
}

function encodeEmailKey(email: string): string {
  return email.replace(/\./g, ",");
}

function maskEmail(email: string): string {
  const [local, domain] = email.split("@");
  const maskLength =
    local.length <= 3 ? 1 :
    local.length <= 5 ? 2 :
    3;
  const visible = local.slice(0, local.length - maskLength);
  return `${visible}${"*".repeat(maskLength)}@${domain}`;
}

async function verifyStoredCode(
  phoneNumber: string,
  code: string,
  expectedPurpose: Purpose
): Promise<void> {
  const codeRef = rtdb.ref(`verificationCodes/${encodeKey(phoneNumber)}`);
  const snap = await codeRef.get();

  if (!snap.exists()) {
    throw new HttpsError("not-found", "인증 요청 내역이 없습니다.");
  }

  const data = snap.val();

  if (data.purpose !== expectedPurpose) {
    throw new HttpsError("failed-precondition", "잘못된 인증 요청입니다. 다시 인증번호를 요청해주세요.");
  }

  if ((data.attempts ?? 0) >= MAX_ATTEMPTS) {
    throw new HttpsError("resource-exhausted", "시도 횟수를 초과했습니다.");
  }

  if (data.expiresAt < Date.now()) {
    throw new HttpsError("deadline-exceeded", "인증번호가 만료되었습니다.");
  }

  if (data.code !== hashCode(code)) {
    await codeRef.update({ attempts: (data.attempts ?? 0) + 1 });
    throw new HttpsError("permission-denied", "인증번호가 일치하지 않습니다.");
  }

  await codeRef.update({ verified: true });
}

// 사용자가 아직 받지 못한 번호 중에서만 랜덤 선택. 다 모았으면 ALL_COLLECTED 에러
async function pickCookieMessage(
  uid: string,
  type: number
): Promise<{ cookieNo: number; message: string }> {
  const [allSnap, collectedSnap] = await Promise.all([
    rtdb.ref(`cookieMessages/${type}`).get(),
    rtdb.ref(`userCollectedCookies/${uid}/${type}`).get(),
  ]);

  if (!allSnap.exists()) {
    throw new HttpsError("not-found", `쿠키 메시지를 찾을 수 없습니다. (type: ${type})`);
  }

  const allMessages = allSnap.val() as Record<string, { text: string }>;
  const allKeys = Object.keys(allMessages);
  const collectedKeys = collectedSnap.exists()
    ? Object.keys(collectedSnap.val())
    : [];
  const remainingKeys = allKeys.filter((k) => !collectedKeys.includes(k));

  if (remainingKeys.length === 0) {
    throw new HttpsError(
      "failed-precondition",
      "이미 모든 메시지를 수집했습니다",
      { reason: "ALL_COLLECTED" }
    );
  }

  const randomKey =
    remainingKeys[Math.floor(Math.random() * remainingKeys.length)];

  return {
    cookieNo: parseInt(randomKey),
    message: allMessages[randomKey].text,
  };
}

async function findEarliestApplicableTicketGroup(
  uid: string,
  type: number
): Promise<string | null> {
  const snap = await rtdb
    .ref(`eventTicketBalance/${uid}`)
    .orderByChild("issuedAt")
    .get();
  if (!snap.exists()) return null;

  let result: string | null = null;
  snap.forEach((child) => {
    if (result !== null) return true;
    const data = child.val();
    if (data.appliesToType === type && (data.available ?? 0) > 0) {
      result = child.key;
    }
    return false;
  });
  return result;
}

export const sendVerificationCode = onCall(
  {
    region: "asia-northeast3",
    secrets: ["SOLAPI_API_KEY", "SOLAPI_API_SECRET", "SOLAPI_SENDER"],
  },
  async (request) => {
    if (!request.auth) {
      throw new HttpsError("unauthenticated", "인증되지 않은 요청입니다.");
    }

    const { phoneNumber, purpose } = request.data as {
      phoneNumber?: string;
      purpose?: Purpose;
    };

    if (!phoneNumber || !/^\+82\d{9,10}$/.test(phoneNumber)) {
      throw new HttpsError("invalid-argument", "전화번호 형식이 올바르지 않습니다.");
    }

    if (!purpose || !["SIGNUP", "FIND_EMAIL", "RESET_PASSWORD"].includes(purpose)) {
      throw new HttpsError("invalid-argument", "purpose 값이 올바르지 않습니다.");
    }

    const domesticPhone = toDomesticFormat(phoneNumber);

    if (purpose === "SIGNUP") {
      const existingSnap = await rtdb.ref(`phoneToUid/${encodeKey(domesticPhone)}`).get();
      if (existingSnap.exists()) {
        throw new HttpsError("already-exists", "이미 가입된 휴대폰 번호입니다.");
      }
    } else {
      const existingSnap = await rtdb.ref(`phoneToUid/${encodeKey(domesticPhone)}`).get();
      if (!existingSnap.exists()) {
        throw new HttpsError("not-found", "등록되지 않은 전화번호입니다.");
      }
    }

    const codeRef = rtdb.ref(`verificationCodes/${encodeKey(phoneNumber)}`);
    const snap = await codeRef.get();

    if (snap.exists()) {
      const createdAt = snap.val().createdAt as number;
      if (createdAt && (Date.now() - createdAt) / 1000 < RESEND_COOLDOWN_SECONDS) {
        throw new HttpsError("resource-exhausted", "잠시 후 다시 시도해주세요.");
      }
    }

    const code = generateCode();
    const now = Date.now();

    await codeRef.set({
      code: hashCode(code),
      purpose,
      expiresAt: now + CODE_EXPIRY_SECONDS * 1000,
      attempts: 0,
      verified: false,
      createdAt: now,
    });

    try {
      await sendSms(phoneNumber, `[인증번호] ${code}를 입력해주세요. 3분 내 유효합니다.`);
    } catch (e: any) {
      console.error("Solapi 발송 실패:", e.response?.data ?? e.message ?? e);
      throw new HttpsError("internal", "SMS 발송에 실패했습니다.");
    }

    return { success: true };
  }
);

export const verifyPhoneForSignup = onCall(
  { region: "asia-northeast3" },
  async (request) => {
    if (!request.auth) {
      throw new HttpsError("unauthenticated", "인증되지 않은 요청입니다.");
    }

    const { phoneNumber, code } = request.data;
    if (!phoneNumber || !code) {
      throw new HttpsError("invalid-argument", "필수 값이 누락되었습니다.");
    }

    await verifyStoredCode(phoneNumber, code, "SIGNUP");
    return { success: true };
  }
);

// 서버사이드 회원가입: 계정 생성 + users/emails/phoneToUid 원자적 저장
export const signUp = onCall(
  { region: "asia-northeast3" },
  async (request) => {
    if (!request.auth) {
      throw new HttpsError("unauthenticated", "인증되지 않은 요청입니다.");
    }

    const { email, password, phone, nickname, birth, gender } = request.data as {
      email?: string;
      password?: string;
      phone?: string;
      nickname?: string;
      birth?: string;
      gender?: boolean;
    };

    if (!email || !password || !phone || !nickname || !birth || gender === undefined) {
      throw new HttpsError("invalid-argument", "필수 값이 누락되었습니다.");
    }

    if (!/^\+82\d{9,10}$/.test(phone)) {
      throw new HttpsError("invalid-argument", "전화번호 형식이 올바르지 않습니다.");
    }

    // 1. 휴대폰 인증 완료 여부 확인 (verifyPhoneForSignup에서 verified: true로 마킹됨)
    const codeRef = rtdb.ref(`verificationCodes/${encodeKey(phone)}`);
    const codeSnap = await codeRef.get();

    if (!codeSnap.exists()) {
      throw new HttpsError("permission-denied", "휴대폰 인증을 먼저 완료해주세요.");
    }

    const codeData = codeSnap.val();
    if (codeData.purpose !== "SIGNUP" || codeData.verified !== true) {
      throw new HttpsError("permission-denied", "휴대폰 인증을 먼저 완료해주세요.");
    }

    if (codeData.expiresAt < Date.now()) {
      throw new HttpsError("deadline-exceeded", "인증이 만료되었습니다. 다시 인증해주세요.");
    }

    // 2. 중복 가입 재확인 (발송 시점과 가입 시점 사이 동시 요청 대비)
    const domesticPhone = toDomesticFormat(phone);
    const phoneIndexSnap = await rtdb.ref(`phoneToUid/${encodeKey(domesticPhone)}`).get();
    if (phoneIndexSnap.exists()) {
      throw new HttpsError("already-exists", "이미 가입된 휴대폰 번호입니다.", { reason: "PHONE_DUPLICATED" });
    }

    // 3. Firebase Auth 계정 생성 (이메일 중복/형식/비밀번호 강도는 Admin SDK가 검증)
    let uid: string;
    try {
      const userRecord = await admin.auth().createUser({ email, password });
      uid = userRecord.uid;
    } catch (e: any) {
      switch (e.code) {
        case "auth/email-already-exists":
          throw new HttpsError("already-exists", "이미 가입된 이메일입니다.", { reason: "EMAIL_DUPLICATED" });
        case "auth/invalid-email":
          throw new HttpsError("invalid-argument", "이메일 형식이 올바르지 않습니다.", { reason: "INVALID_EMAIL" });
        case "auth/invalid-password":
          throw new HttpsError("invalid-argument", "비밀번호가 너무 짧습니다.", { reason: "WEAK_PASSWORD" });
        default:
          console.error("계정 생성 실패:", e);
          throw new HttpsError("internal", "계정 생성에 실패했습니다.");
      }
    }

    // 4. RTDB atomic write: users/{uid} + emails/{encodedEmail} + phoneToUid/{encodedPhone}
    const userInfo = { email, nickname, phone, birth, gender };
    const updates: Record<string, unknown> = {
      [`users/${uid}`]: userInfo,
      [`emails/${encodeEmailKey(email)}`]: true,
      [`phoneToUid/${encodeKey(domesticPhone)}`]: uid,
    };

    try {
      await rtdb.ref().update(updates);
    } catch (e) {
      console.error("DB 저장 실패, 계정 롤백:", e);
      await admin.auth().deleteUser(uid).catch((err) => console.error("계정 롤백 실패:", err));
      throw new HttpsError("internal", "회원정보 저장에 실패했습니다.", { reason: "DB_SAVE_FAILED" });
    }

    await codeRef.remove();

    return { success: true };
  }
);

export const verifyCodeAndFindEmail = onCall(
  { region: "asia-northeast3" },
  async (request) => {
    if (!request.auth) {
      throw new HttpsError("unauthenticated", "인증되지 않은 요청입니다.");
    }

    const { phoneNumber, code } = request.data;
    if (!phoneNumber || !code) {
      throw new HttpsError("invalid-argument", "필수 값이 누락되었습니다.");
    }

    await verifyStoredCode(phoneNumber, code, "FIND_EMAIL");

    const domesticPhone = toDomesticFormat(phoneNumber);
    const phoneIndexSnap = await rtdb.ref(`phoneToUid/${encodeKey(domesticPhone)}`).get();

    if (!phoneIndexSnap.exists()) {
      throw new HttpsError("not-found", "일치하는 계정을 찾을 수 없습니다.");
    }

    const uid = phoneIndexSnap.val();
    const userSnap = await rtdb.ref(`users/${uid}`).get();

    if (!userSnap.exists()) {
      throw new HttpsError("not-found", "일치하는 계정을 찾을 수 없습니다.");
    }

    await rtdb.ref(`verificationCodes/${encodeKey(phoneNumber)}`).remove();
    return { maskedEmail: maskEmail(userSnap.val().email) };
  }
);

export const verifyCodeForResetPassword = onCall(
  { region: "asia-northeast3" },
  async (request) => {
    if (!request.auth) {
      throw new HttpsError("unauthenticated", "인증되지 않은 요청입니다.");
    }

    const { phoneNumber, code } = request.data;
    if (!phoneNumber || !code) {
      throw new HttpsError("invalid-argument", "필수 값이 누락되었습니다.");
    }

    await verifyStoredCode(phoneNumber, code, "RESET_PASSWORD");

    const domesticPhone = toDomesticFormat(phoneNumber);
    const phoneIndexSnap = await rtdb.ref(`phoneToUid/${encodeKey(domesticPhone)}`).get();

    if (!phoneIndexSnap.exists()) {
      throw new HttpsError("not-found", "일치하는 계정을 찾을 수 없습니다.");
    }

    return { success: true };
  }
);

export const resetPassword = onCall(
  { region: "asia-northeast3" },
  async (request) => {
    if (!request.auth) {
      throw new HttpsError("unauthenticated", "인증되지 않은 요청입니다.");
    }

    const { phoneNumber, newPassword } = request.data;
    if (!phoneNumber || !newPassword || newPassword.length < 8) {
      throw new HttpsError("invalid-argument", "필수 값이 누락되었거나 비밀번호가 너무 짧습니다.");
    }

    const codeRef = rtdb.ref(`verificationCodes/${encodeKey(phoneNumber)}`);
    const snap = await codeRef.get();

    if (!snap.exists()) {
      throw new HttpsError("permission-denied", "휴대폰 인증을 먼저 완료해주세요.");
    }

    const data = snap.val();

    if (data.purpose !== "RESET_PASSWORD" || data.verified !== true) {
      throw new HttpsError("permission-denied", "휴대폰 인증을 먼저 완료해주세요.");
    }

    if (data.expiresAt < Date.now()) {
      throw new HttpsError("deadline-exceeded", "인증이 만료되었습니다. 다시 인증해주세요.");
    }

    const domesticPhone = toDomesticFormat(phoneNumber);
    const phoneIndexSnap = await rtdb.ref(`phoneToUid/${encodeKey(domesticPhone)}`).get();

    if (!phoneIndexSnap.exists()) {
      throw new HttpsError("not-found", "일치하는 계정을 찾을 수 없습니다.");
    }

    const uid = phoneIndexSnap.val();
    await admin.auth().updateUser(uid, { password: newPassword });
    await codeRef.remove();
    return { success: true };
  }
);

// 쿠키 이벤트 서버 동기화 (멱등성, 일일 제한, 티켓 차감, 중복 없는 랜덤 선택)
export const syncCookieEvent = onCall(
  { region: "asia-northeast3" },
  async (request) => {
    if (!request.auth) {
      throw new HttpsError("unauthenticated", "인증되지 않은 요청입니다.");
    }

    const uid = request.auth.uid;
    const { eventId, type, datetime } = request.data as {
      eventId?: string;
      type?: number;
      datetime?: string;
    };

    if (!eventId || type === undefined || !datetime) {
      throw new HttpsError("invalid-argument", "필수 값이 누락되었습니다.");
    }

    // 멱등성 체크: 이미 처리된 이벤트인지 확인
    const idempotencyRef = rtdb.ref(`cookieEvents/${uid}/${eventId}`);
    const existing = await idempotencyRef.get();
    if (existing.exists()) {
      const data = existing.val();
      const result: Record<string, unknown> = {
        cookieNo: data.cookieNo,
        message: data.message,
      };
      if (data.viaTicket) result["viaTicket"] = data.viaTicket;
      return result;
    }

    const date = datetime.substring(0, 8); // yyyyMMdd

    // 일일 제한 트랜잭션
    const claimRef = rtdb.ref(`dailyCookieClaims/${uid}/${date}/${type}`);
    const claimResult = await claimRef.transaction((current: number | null) => {
      if (current !== null) return undefined;
      return 1;
    });

    if (claimResult.committed) {
      const { cookieNo, message } = await pickCookieMessage(uid, type);
      await Promise.all([
        idempotencyRef.set({ type, cookieNo, message, datetime, claimDate: date }),
        rtdb.ref(`userCollectedCookies/${uid}/${type}/${cookieNo}`).set(true),
      ]);
      return { cookieNo, message };
    }

    // 일일 제한 초과 → 티켓 소모 시도
    const ticketGroupId = await findEarliestApplicableTicketGroup(uid, type);
    if (!ticketGroupId) {
      throw new HttpsError("failed-precondition", "일일 제한 초과", {
        reason: "DAILY_LIMIT_AND_NO_TICKET",
      });
    }

    const ticketRef = rtdb.ref(
      `eventTicketBalance/${uid}/${ticketGroupId}/available`
    );
    const ticketResult = await ticketRef.transaction(
      (current: number | null) => {
        if (current === null || current <= 0) return undefined;
        return current - 1;
      }
    );

    if (!ticketResult.committed) {
      throw new HttpsError(
        "failed-precondition",
        "티켓 소진 (동시 요청)",
        { reason: "TICKET_EXHAUSTED_CONCURRENT" }
      );
    }

    const { cookieNo, message } = await pickCookieMessage(uid, type);
    await Promise.all([
      idempotencyRef.set({
        type,
        cookieNo,
        message,
        datetime,
        claimDate: date,
        viaTicket: ticketGroupId,
      }),
      rtdb.ref(`userCollectedCookies/${uid}/${type}/${cookieNo}`).set(true),
      rtdb.ref(`ticketUsageLog/${uid}/${eventId}`).set({
        ticketGroupId,
        usedAt: admin.database.ServerValue.TIMESTAMP,
      }),
    ]);

    return { cookieNo, message, viaTicket: ticketGroupId };
  }
);

// 타입별 최대 쿠키 메시지 개수 — 앱 실행 시 1회 호출해 로컬에 캐시
export const getCookieTypeCounts = onCall(
  { region: "asia-northeast3" },
  async (request) => {
    if (!request.auth) {
      throw new HttpsError("unauthenticated", "인증되지 않은 요청입니다.");
    }

    const snap = await rtdb.ref("cookieMessages").get();
    if (!snap.exists()) {
      return { counts: {} };
    }

    const counts: Record<string, number> = {};
    snap.forEach((typeChild) => {
      counts[typeChild.key!] = typeChild.numChildren();
      return false;
    });

    return { counts };
  }
);

// 로그인 시 계정의 전체 쿠키 오픈 히스토리를 클라이언트로 내려줌 (서버→로컬 동기화용)
export const fetchCookieEvents = onCall(
  { region: "asia-northeast3" },
  async (request) => {
    if (!request.auth) {
      throw new HttpsError("unauthenticated", "인증되지 않은 요청입니다.");
    }

    const uid = request.auth.uid;
    const snap = await rtdb.ref(`cookieEvents/${uid}`).get();

    if (!snap.exists()) {
      return { events: [] };
    }

    const events: Record<string, unknown>[] = [];
    snap.forEach((child) => {
      const data = child.val();
      events.push({
        eventId: child.key,
        type: data.type,
        cookieNo: data.cookieNo ?? null,
        message: data.message ?? null,
        datetime: data.datetime,
        claimDate: data.claimDate,
        viaTicket: data.viaTicket ?? null,
      });
      return false;
    });

    return { events };
  }
);

// 매일 1회: 30일 지난 익명 계정 자동 삭제
export const cleanupAnonymousUsers = onSchedule(
  { schedule: "every 24 hours", region: "asia-northeast3" },
  async () => {
    const auth = admin.auth();
    let nextPageToken: string | undefined;
    const cutoff = Date.now() - 30 * 24 * 60 * 60 * 1000;

    do {
      const result = await auth.listUsers(1000, nextPageToken);
      const toDelete = result.users
        .filter(
          (u) =>
            u.providerData.length === 0 &&
            new Date(u.metadata.creationTime).getTime() < cutoff
        )
        .map((u) => u.uid);

      if (toDelete.length > 0) {
        await auth.deleteUsers(toDelete);
        console.log(`삭제된 익명 계정 수: ${toDelete.length}`);
      }

      nextPageToken = result.pageToken;
    } while (nextPageToken);
  }
);