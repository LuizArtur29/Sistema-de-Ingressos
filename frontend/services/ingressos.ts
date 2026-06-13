import api from "./http";
import { IngressoResponse } from "./types";

export async function listarIngressosPorSessao(sessaoId: number): Promise<IngressoResponse[]> {
  const { data } = await api.get<IngressoResponse[]>(`/api/ingressos/sessoes/${sessaoId}`);
  return data;
}

export async function registrarEntrada(ingressoId: number): Promise<void> {
  await api.put(`/api/ingressos/${ingressoId}/entrada`);
}
