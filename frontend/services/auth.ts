import api from "./http";
import { LoginRequest, LoginResponse, RegisterRequest, RegisterResponse } from "./types";

export async function login(payload: LoginRequest): Promise<LoginResponse> {
    const { data } = await api.post<LoginResponse>("/api/auth/login", payload);
    return data;
}

export async function register(payload: RegisterRequest): Promise<RegisterResponse> {
    const { data } = await api.post<RegisterResponse>("/api/usuarios", payload);
    return data;
}