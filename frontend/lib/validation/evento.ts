import { EventoStatus } from "@/services/types";

export type EventoFormState = {
  nome: string;
  descricao: string;
  dataInicio: string;
  dataFim: string;
  local: string;
  capacidadeTotal: string | number;
  status: EventoStatus | string;
};

export type EventoFieldErrors<T extends EventoFormState = EventoFormState> = Partial<Record<keyof T, string>>;

export const EVENTO_STATUS_VALIDOS: EventoStatus[] = ["ATIVO", "CANCELADO", "FINALIZADO"];

function isPositiveInteger(value: string | number) {
  const normalized = String(value).trim();
  const parsed = Number(normalized);

  return normalized !== "" && Number.isInteger(parsed) && parsed > 0;
}

export function validateEventoForm<T extends EventoFormState>(form: T): EventoFieldErrors<T> {
  const errors: EventoFieldErrors<T> = {};

  if (!form.nome.trim()) errors.nome = "Informe o nome do evento.";
  if (!form.descricao.trim()) errors.descricao = "Informe a descrição do evento.";
  if (!form.dataInicio) errors.dataInicio = "Informe a data de início.";
  if (!form.dataFim) errors.dataFim = "Informe a data de término.";
  if (form.dataInicio && form.dataFim && form.dataFim < form.dataInicio) {
    errors.dataFim = "A data de término não pode ser anterior à data de início.";
  }
  if (!form.local.trim()) errors.local = "Informe o local do evento.";
  if (String(form.capacidadeTotal).trim() === "") {
    errors.capacidadeTotal = "Informe a capacidade total.";
  } else if (!isPositiveInteger(form.capacidadeTotal)) {
    errors.capacidadeTotal = "A capacidade total deve ser maior que zero.";
  }
  if (!EVENTO_STATUS_VALIDOS.includes(form.status as EventoStatus)) {
    errors.status = "Selecione um status válido.";
  }

  return errors;
}
