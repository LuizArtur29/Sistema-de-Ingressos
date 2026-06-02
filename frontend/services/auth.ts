import api from "./http";
import { LoginRequest, LoginResponse, RegisterRequest, RegisterResponse, UsuarioAutenticado } from "./types";

export async function login(payload: LoginRequest): Promise<LoginResponse> {
    const { data } = await api.post<LoginResponse>("/api/auth/login", payload);
    return data;
}

export async function register(payload: RegisterRequest): Promise<RegisterResponse> {
    const { data } = await api.post<RegisterResponse>("/api/usuarios", payload);
    return data;
}

import { UsuarioPerfil } from "./types";

export async function getMyProfile(): Promise<UsuarioAutenticado> {
    const { data } = await api.get<UsuarioAutenticado>("/api/usuarios/me");
    return data;
}