import { EventoResponse } from "@/services/types";
import { EventFormState } from "./eventDetailsTypes";

export function normalizeDate(value?: string) {
  if (!value) return "";
  return value.slice(0, 10);
}

export function toEventFormState(evento: EventoResponse): EventFormState {
  return {
    nome: evento.nome,
    descricao: evento.descricao,
    dataInicio: normalizeDate(evento.dataInicio),
    dataFim: normalizeDate(evento.dataFim),
    local: evento.local,
    capacidadeTotal: String(evento.capacidadeTotal),
    status: evento.status,
  };
}

export function toLocalDateTime(value: string) {
  if (!value) return "";
  if (value.length === 19) return value;
  return `${value}:00`;
}

export function toDateTimeLocal(value: string) {
  if (!value) return "";
  return value.slice(0, 16);
}
