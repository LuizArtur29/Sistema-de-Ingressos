import api from "./http";
import { TipoIngresso, TipoIngressoPayload } from "./types";

export async function listarTiposPorSessao(sessaoId: number): Promise<TipoIngresso[]> {
    const { data } = await api.get<TipoIngresso[]>(`/api/tipos-ingresso/sessao/${sessaoId}`);
    return data;
}

export async function criarTipoIngresso(payload: TipoIngressoPayload): Promise<TipoIngresso> {
    const { data } = await api.post<TipoIngresso>("/api/tipos-ingresso", payload);
    return data;
}