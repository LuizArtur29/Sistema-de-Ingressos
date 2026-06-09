import { ApiProblemDetail } from "@/services/types";

export function mapApiFieldErrors<T extends string>(
  data: ApiProblemDetail | undefined,
  fieldNames: readonly T[]
): { fieldErrors: Partial<Record<T, string>>; fallbackMessage: string | null } {
  const fieldErrors: Partial<Record<T, string>> = {};
  const unmappedErrors: string[] = [];

  data?.errors?.forEach((item) => {
    if (fieldNames.includes(item.field as T)) {
      fieldErrors[item.field as T] = item.message;
      return;
    }
    unmappedErrors.push(item.message);
  });

  return {
    fieldErrors,
    fallbackMessage: unmappedErrors[0] ?? data?.detail ?? null,
  };
}
