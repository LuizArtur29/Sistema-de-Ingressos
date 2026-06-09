import { isAxiosError } from "axios";

export function isForbiddenError(err: unknown) {
  return isAxiosError(err) && err.response?.status === 403;
}

export function getForbiddenMessage() {
  return "Você não tem permissão para realizar esta ação.";
}
