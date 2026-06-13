import api from "./http";
import { TransferenciaRequest, TransferenciaResponse } from "./types";

export async function transferirIngresso(payload: TransferenciaRequest): Promise<TransferenciaResponse> {
  const { data } = await api.post<TransferenciaResponse>("/api/transferencias", payload);
  return data;
}
