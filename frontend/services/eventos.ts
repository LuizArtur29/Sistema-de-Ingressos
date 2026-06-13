import api from "./http";
import { EventoResponse, EventoCreateRequest } from "./types";

export async function listarEventos(): Promise<EventoResponse[]> {
    const { data } = await api.get<EventoResponse[]>("/api/eventos");
    return data;
}

export async function listarMeusEventos(): Promise<EventoResponse[]> {
    const { data } = await api.get<EventoResponse[]>("/api/eventos/meus");
    return data;
}

export async function buscarEventoPorId(id: number): Promise<EventoResponse> {
    const { data } = await api.get<EventoResponse>(`/api/eventos/${id}`);
    return data;
}

export async function criarEvento(payload: EventoCreateRequest): Promise<EventoResponse> {
    const { data } = await api.post<EventoResponse>("/api/eventos", payload);
    return data;
}

export async function atualizarEvento(id: number, payload: EventoCreateRequest): Promise<EventoResponse> {
    const { data } = await api.put<EventoResponse>(`/api/eventos/${id}`, payload);
    return data;
}

export async function excluirEvento(id: number): Promise<void> {
    await api.delete(`/api/eventos/${id}`);
}
