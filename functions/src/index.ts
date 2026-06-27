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

// +821012345678 -> 01012345678 (users/phoneToUid 저장 형식과 통일)
function toDomesticFormat(e164Phone: string): string {
  return e164Phone.replace(/^\+82/, "0");
}

// test01@test.com -> tes***@test.com
function maskEmail(email: string): string {
  const [local, domain] = email.split("@");

  const maskLength =
    local.length <= 3 ? 1 :
    local.length <= 5 ? 2 :
    3;

  const visible = local.slice(0, local.length - maskLength);
  return `${visible}${"*".repeat(maskLength)}@${domain}`;
}

// 공통: 인증번호 검증 (purpose 일치 여부까지 확인)
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
    throw new HttpsError(
      "failed-precondition",
      "잘못된 인증 요청입니다. 다시 인증번호를 요청해주세요."
    );
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

// 1) 인증번호 발송 (회원가입 / 이메일 찾기 / 비밀번호 재설정 공용)
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
      // FIND_EMAIL / RESET_PASSWORD: 가입된 번호여야만 발송
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

// 2) 회원가입용: 인증번호 확인만 (이메일 조회 없음)
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

// 3) 이메일 찾기용: 인증번호 확인 + 마스킹된 이메일 조회
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

    // 인증 완료 후 코드 즉시 폐기 (재사용 방지)
    await rtdb.ref(`verificationCodes/${encodeKey(phoneNumber)}`).remove();

    return { maskedEmail: maskEmail(userSnap.val().email) };
  }
);

// 4) 비밀번호 재설정용: 인증번호 확인만 (비밀번호 변경은 별도 단계)
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

// 5) 비밀번호 재설정 실행 — verified=true + purpose 일치 확인 후에만 변경
export const resetPassword = onCall(
  { region: "asia-northeast3" },
  async (request) => {
    if (!request.auth) {
      throw new HttpsError("unauthenticated", "인증되지 않은 요청입니다.");
    }

    const { phoneNumber, newPassword } = request.data;
    if (!phoneNumber || !newPassword || newPassword.length < 8) {
      throw new HttpsError(
        "invalid-argument",
        "필수 값이 누락되었거나 비밀번호가 너무 짧습니다."
      );
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
    await codeRef.remove(); // 재사용 방지

    return { success: true };
  }
);

// 6) 매일 1회: 30일 지난 익명 계정 자동 삭제
export const cleanupAnonymousUsers = onSchedule(
  { schedule: "every 24 hours", region: "asia-northeast3" },
  async () => {
    const auth = admin.auth();
    let nextPageToken: string | undefined;
    const cutoff = Date.now() - 30 * 24 * 60 * 60 * 1000; // 30일 전

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