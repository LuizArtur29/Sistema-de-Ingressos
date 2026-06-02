import api from "./http";
import { SessaoEventoResponse, SessaoEventoRequest } from "./types";

export async function listarSessoesPorEvento(eventoId: number): Promise<SessaoEventoResponse[]> {
    const { data } = await api.get<SessaoEventoResponse[]>(`/api/sessoes-evento/evento/${eventoId}`);
    return data;
}

export async function criarSessao(payload: SessaoEventoRequest): Promise<SessaoEventoResponse> {
    const { data } = await api.post<SessaoEventoResponse>("/api/sessoes-evento", payload);
    return data;
}

export async function atualizarSessao(id: number, payload: SessaoEventoRequest): Promise<SessaoEventoResponse> {
    const { data } = await api.put<SessaoEventoResponse>(`/api/sessoes-evento/${id}`, payload);
    return data;
}

export async function excluirSessao(id: number): Promise<void> {
    await api.delete(`/api/sessoes-evento/${id}`);
}