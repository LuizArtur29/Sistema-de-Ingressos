import axios from "axios";

const api = axios.create({
    baseURL: process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080",
    headers: {
        "Content-Type": "application/json",
    },
});

function parseJwt(token: string): { exp?: number } | null {
    try {
        if (typeof window === "undefined") return null;
        const payload = token.split(".")[1];
        if (!payload) return null;
        const base64 = payload.replace(/-/g, "+").replace(/_/g, "/");
        const json = decodeURIComponent(
            atob(base64)
                .split("")
                .map((c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
                .join("")
        );
        return JSON.parse(json);
    } catch {
        return null;
    }
}

function isTokenValid(token: string): boolean {
    if (!token || token.trim().length < 20) return false;
    const payload = parseJwt(token);
    if (!payload?.exp) return false;
    const now = Math.floor(Date.now() / 1000);
    return payload.exp > now;
}

api.interceptors.request.use((config) => {
    const token = sessionStorage.getItem("token");

    if (token && isTokenValid(token)) {
        config.headers.Authorization = `Bearer ${token}`;
    } else {
        sessionStorage.removeItem("token");
        if (typeof window !== "undefined") {
            window.dispatchEvent(new Event("session-expired"));
        }
    }

    return config;
});

api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            sessionStorage.removeItem("token");
            if (typeof window !== "undefined") {
                window.dispatchEvent(new Event("session-expired"));
            }
        }
        return Promise.reject(error);
    }
);

export default api;