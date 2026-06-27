import * as crypto from "crypto";
import axios from "axios";

const SOLAPI_API_KEY = process.env.SOLAPI_API_KEY!;
const SOLAPI_API_SECRET = process.env.SOLAPI_API_SECRET!;
const SENDER_NUMBER = process.env.SOLAPI_SENDER!;

function getSolapiAuthHeader(): string {
  const date = new Date().toISOString();
  const salt = crypto.randomBytes(16).toString("hex");
  const signature = crypto
    .createHmac("sha256", SOLAPI_API_SECRET)
    .update(date + salt)
    .digest("hex");

  return `HMAC-SHA256 apiKey=${SOLAPI_API_KEY}, date=${date}, salt=${salt}, signature=${signature}`;
}

export async function sendSms(to: string, text: string): Promise<void> {
  const authHeader = getSolapiAuthHeader();

  try {
    await axios.post(
      "https://api.solapi.com/messages/v4/send",
      {
        message: {
          to: to.replace("+82", "0"),
          from: SENDER_NUMBER,
          text,
        },
      },
      {
        headers: {
          Authorization: authHeader,
          "Content-Type": "application/json",
        },
      }
    );
  } catch (e: any) {
    console.error("Solapi API 응답 에러:", JSON.stringify(e.response?.data ?? e.message));
    throw e;
  }
}