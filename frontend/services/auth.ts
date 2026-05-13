import api from "./http";
import { LoginRequest, LoginResponse } from "./types";

export async function login(payload: LoginRequest): Promise<LoginResponse> {
    const { data } = await api.post<LoginResponse>("/api/auth/login", payload);
    return data;
}