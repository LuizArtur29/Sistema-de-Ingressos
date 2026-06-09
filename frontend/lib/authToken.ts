export type JwtPayload = {
  exp?: number;
};

type SessionExpiredReason = "missing" | "expired";

let sessionExpiredDispatched = false;

export function parseJwt(token: string): JwtPayload | null {
  try {
    if (typeof window === "undefined") return null;
    const payload = token.split(".")[1];
    if (!payload) return null;
    const base64 = payload.replace(/-/g, "+").replace(/_/g, "/");
    const json = decodeURIComponent(
      atob(base64)
        .split("")
        .map((char) => "%" + ("00" + char.charCodeAt(0).toString(16)).slice(-2))
        .join("")
    );

    return JSON.parse(json);
  } catch {
    return null;
  }
}

export function isTokenValid(token: string | null | undefined): boolean {
  if (!token || token.trim().length < 20) return false;
  const payload = parseJwt(token);
  if (!payload?.exp) return false;

  const now = Math.floor(Date.now() / 1000);
  return payload.exp > now;
}

export function getStoredToken() {
  if (typeof window === "undefined") return null;
  return sessionStorage.getItem("token");
}

export function storeToken(token: string) {
  sessionExpiredDispatched = false;
  sessionStorage.setItem("token", token);
}

export function clearToken() {
  if (typeof window === "undefined") return;
  sessionStorage.removeItem("token");
}

export function expireSession(reason: SessionExpiredReason = "expired") {
  if (typeof window === "undefined") return;

  clearToken();

  if (sessionExpiredDispatched) return;

  sessionExpiredDispatched = true;
  window.dispatchEvent(new CustomEvent("session-expired", { detail: { reason } }));
}

// Future hardening: move the token out of sessionStorage into an HttpOnly secure cookie.
