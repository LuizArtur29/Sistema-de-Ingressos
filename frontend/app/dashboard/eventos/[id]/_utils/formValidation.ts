import {
  SESSION_STATUS_OPTIONS,
  SessionFieldErrors,
  SessionStatus,
  TicketTypeFieldErrors,
} from "./eventDetailsTypes";

function isPositiveIntegerText(value: string) {
  const normalized = value.trim();
  const parsed = Number(normalized);

  return normalized !== "" && Number.isInteger(parsed) && parsed > 0;
}

function isNonNegativeNumberText(value: string) {
  const normalized = value.trim();
  const parsed = Number(normalized);

  return normalized !== "" && Number.isFinite(parsed) && parsed >= 0;
}

export function validateSessionForm(
  nomeSessao: string,
  dataHoraSessao: string,
  statusSessao: string,
  capacidade: string
): SessionFieldErrors {
  const errors: SessionFieldErrors = {};

  if (!nomeSessao.trim()) errors.nomeSessao = "Informe o nome da sessão.";
  if (!dataHoraSessao) errors.dataHoraSessao = "Informe a data e hora da sessão.";
  if (!SESSION_STATUS_OPTIONS.includes(statusSessao as SessionStatus)) {
    errors.statusSessao = "Selecione um status válido para a sessão.";
  }
  if (capacidade.trim() && !isPositiveIntegerText(capacidade)) {
    errors.capacidade = "A capacidade da sessão deve ser maior que zero.";
  }

  return errors;
}

export function validateTicketTypeForm(
  nomeSetor: string,
  preco: string,
  quantidadeTotal: string,
  lote: string
): TicketTypeFieldErrors {
  const errors: TicketTypeFieldErrors = {};

  if (!nomeSetor.trim()) errors.nomeSetor = "Informe o setor.";
  if (!isNonNegativeNumberText(preco)) errors.preco = "O preço deve ser maior ou igual a zero.";
  if (!isPositiveIntegerText(quantidadeTotal)) {
    errors.quantidadeTotal = "A quantidade deve ser maior que zero.";
  }
  if (!isPositiveIntegerText(lote)) errors.lote = "O lote deve ser maior que zero.";

  return errors;
}
