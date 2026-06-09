import axios from "axios";
import { expireSession, getStoredToken, isTokenValid } from "@/lib/authToken";

const api = axios.create({
    baseURL: process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080",
    headers: {
        "Content-Type": "application/json",
    },
});

api.interceptors.request.use((config) => {
    const token = getStoredToken();

    if (token) {
        if (isTokenValid(token)) {
            config.headers.Authorization = `Bearer ${token}`;
        } else {
            expireSession("expired");
        }
    }

    return config;
});

api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            expireSession("expired");
        }
        return Promise.reject(error);
    }
);

export default api;
