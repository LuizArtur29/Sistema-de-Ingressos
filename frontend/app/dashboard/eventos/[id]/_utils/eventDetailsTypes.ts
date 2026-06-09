import { EventoCreateRequest, EventoStatus } from "@/services/types";
import { EventoFieldErrors } from "@/lib/validation/evento";

export type EventFormState = Omit<EventoCreateRequest, "capacidadeTotal"> & {
  capacidadeTotal: string;
};

export type EventFieldErrors = EventoFieldErrors<EventFormState>;
export type SessionStatus = "ATIVO" | "ESGOTADO" | "CANCELADO";
export type SessionFieldErrors = Partial<Record<"nomeSessao" | "dataHoraSessao" | "statusSessao" | "capacidade", string>>;
export type TicketTypeFieldErrors = Partial<Record<"nomeSetor" | "preco" | "quantidadeTotal" | "lote", string>>;

export const initialEventFormState: EventFormState = {
  nome: "",
  descricao: "",
  dataInicio: "",
  dataFim: "",
  local: "",
  capacidadeTotal: "",
  status: "ATIVO" as EventoStatus,
};

export const EVENT_FIELD_NAMES = ["nome", "descricao", "dataInicio", "dataFim", "local", "capacidadeTotal", "status"] as const;
export const SESSION_FIELD_NAMES = ["nomeSessao", "dataHoraSessao", "statusSessao", "capacidade"] as const;
export const TICKET_TYPE_FIELD_NAMES = ["nomeSetor", "preco", "quantidadeTotal", "lote"] as const;
export const SESSION_STATUS_OPTIONS: SessionStatus[] = ["ATIVO", "ESGOTADO", "CANCELADO"];
