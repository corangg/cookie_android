import { onCall, HttpsError } from "firebase-functions/v2/https";
import * as admin from "firebase-admin";
import * as crypto from "crypto";
import { sendSms } from "./solapi";

admin.initializeApp();
const rtdb = admin.database();

const CODE_EXPIRY_SECONDS = 180;
const MAX_ATTEMPTS = 5;
const RESEND_COOLDOWN_SECONDS = 60;

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

export const sendVerificationCode = onCall(
  {
    region: "asia-northeast3",
    secrets: ["SOLAPI_API_KEY", "SOLAPI_API_SECRET", "SOLAPI_SENDER"],
  },
  async (request) => {
    if (!request.auth) {
      throw new HttpsError("unauthenticated", "인증되지 않은 요청입니다.");
    }

    const { phoneNumber } = request.data;
    if (!phoneNumber || !/^\+82\d{9,10}$/.test(phoneNumber)) {
      throw new HttpsError("invalid-argument", "전화번호 형식이 올바르지 않습니다.");
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

export const verifyCodeAndFindEmail = onCall(
  {
    region: "asia-northeast3",
    secrets: ["SOLAPI_API_KEY", "SOLAPI_API_SECRET", "SOLAPI_SENDER"],
  },
  async (request) => {
    if (!request.auth) {
      throw new HttpsError("unauthenticated", "인증되지 않은 요청입니다.");
    }

    const { phoneNumber, code } = request.data;
    if (!phoneNumber || !code) {
      throw new HttpsError("invalid-argument", "필수 값이 누락되었습니다.");
    }

    const codeRef = rtdb.ref(`verificationCodes/${encodeKey(phoneNumber)}`);
    const snap = await codeRef.get();

    if (!snap.exists()) {
      throw new HttpsError("not-found", "인증 요청 내역이 없습니다.");
    }

    const data = snap.val();

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

    return { email: userSnap.val().email };
  }
);