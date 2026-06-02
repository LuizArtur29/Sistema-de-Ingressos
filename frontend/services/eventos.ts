import api from "./http";
import { Evento, EventoPayload } from "./types";

export async function listarEventos(): Promise<Evento[]> {
    const { data } = await api.get<Evento[]>("/api/eventos");
    return data;
}

export async function buscarEventoPorId(id: number): Promise<Evento> {
    const { data } = await api.get<Evento>(`/api/eventos/${id}`);
    return data;
}

export async function criarEvento(payload: EventoPayload): Promise<Evento> {
    const { data } = await api.post<Evento>("/api/eventos", payload);
    return data;
}

export async function atualizarEvento(id: number, payload: EventoPayload): Promise<Evento> {
    const { data } = await api.put<Evento>(`/api/eventos/${id}`, payload);
    return data;
}

export async function excluirEvento(id: number): Promise<void> {
    await api.delete(`/api/eventos/${id}`);
}
