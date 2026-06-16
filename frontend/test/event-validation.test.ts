import { describe, expect, it } from "vitest";
import { validateEventoForm } from "@/lib/validation/evento";

describe("validateEventoForm", () => {
  it("valida datas, capacidade e status do evento", () => {
    const errors = validateEventoForm({
      nome: "Festival Recife",
      descricao: "Shows e gastronomia",
      dataInicio: "2026-08-21",
      dataFim: "2026-08-20",
      local: "Marco Zero",
      capacidadeTotal: "0",
      status: "RASCUNHO",
    });

    expect(errors).toEqual({
      dataFim: "A data de término não pode ser anterior à data de início.",
      capacidadeTotal: "A capacidade total deve ser maior que zero.",
      status: "Selecione um status válido.",
    });
  });
});
