import api from "./http";
import { Evento } from "./types";

export async function listarEventos(): Promise<Evento[]> {
    const { data } = await api.get<Evento[]>("/api/eventos");
    return data;
}

export async function criarEvento(payload: Evento): Promise<Evento> {
    const { data } = await api.post<Evento>("/api/eventos", payload);
    return data;
}