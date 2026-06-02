import api from "./http";
import { SessaoEvento, SessaoEventoPayload } from "./types";

export async function listarSessoesPorEvento(eventoId: number): Promise<SessaoEvento[]> {
    const { data } = await api.get<SessaoEvento[]>(`/api/sessoes-evento/evento/${eventoId}`);
    return data;
}

export async function criarSessao(payload: SessaoEventoPayload): Promise<SessaoEvento> {
    const { data } = await api.post<SessaoEvento>("/api/sessoes-evento", payload);
    return data;
}

export async function atualizarSessao(id: number, payload: SessaoEventoPayload): Promise<SessaoEvento> {
    const { data } = await api.put<SessaoEvento>(`/api/sessoes-evento/${id}`, payload);
    return data;
}

export async function excluirSessao(id: number): Promise<void> {
    await api.delete(`/api/sessoes-evento/${id}`);
}