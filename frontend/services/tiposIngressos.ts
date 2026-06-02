import api from "./http";
import { TipoIngressoResponse, TipoIngressoCreateRequest } from "./types";

export async function listarTiposPorSessao(sessaoId: number): Promise<TipoIngressoResponse[]> {
    const { data } = await api.get<TipoIngressoResponse[]>(`/api/tipos-ingresso/sessao/${sessaoId}`);
    return data;
}

export async function criarTipoIngresso(payload: TipoIngressoCreateRequest): Promise<TipoIngressoResponse> {
    const { data } = await api.post<TipoIngressoResponse>("/api/tipos-ingresso", payload);
    return data;
}