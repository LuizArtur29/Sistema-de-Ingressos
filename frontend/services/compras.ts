import api from "./http";
import { CompraRequest, CompraResponse } from "./types";

export async function realizarCompra(payload: CompraRequest): Promise<CompraResponse> {
  const { data } = await api.post<CompraResponse>("/api/compras", payload);
  return data;
}

export async function listarCompras(): Promise<CompraResponse[]> {
  const { data } = await api.get<CompraResponse[]>("/api/compras");
  return data;
}

export async function listarComprasPorUsuario(usuarioId: number): Promise<CompraResponse[]> {
  const { data } = await api.get<CompraResponse[]>(`/api/compras/usuario/${usuarioId}`);
  return data;
}
