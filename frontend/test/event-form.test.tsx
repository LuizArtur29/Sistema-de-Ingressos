import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, expect, it, vi } from "vitest";
import EventForm from "@/app/dashboard/eventos/[id]/_components/EventForm";
import { EventFormState } from "@/app/dashboard/eventos/[id]/_utils/eventDetailsTypes";

const form: EventFormState = {
  nome: "Festival Recife",
  descricao: "Shows e gastronomia",
  dataInicio: "2026-08-20",
  dataFim: "2026-08-21",
  local: "Marco Zero",
  capacidadeTotal: "5000",
  status: "ATIVO",
};

describe("EventForm", () => {
  it("bloqueia edição para usuários sem permissão administrativa", () => {
    render(
      <EventForm
        form={form}
        fieldErrors={{}}
        error={null}
        isAdmin={false}
        saving={false}
        deleting={false}
        onCancel={vi.fn()}
        onSubmit={vi.fn()}
        onFieldChange={vi.fn()}
      />
    );

    expect(screen.getByLabelText(/nome do evento/i)).toBeDisabled();
    expect(screen.queryByRole("button", { name: /salvar alterações/i })).not.toBeInTheDocument();
  });

  it("permite submit de edição para administradores", async () => {
    const user = userEvent.setup();
    const onSubmit = vi.fn((event: React.FormEvent) => event.preventDefault());

    render(
      <EventForm
        form={form}
        fieldErrors={{}}
        error={null}
        isAdmin
        saving={false}
        deleting={false}
        onCancel={vi.fn()}
        onSubmit={onSubmit}
        onFieldChange={vi.fn()}
      />
    );

    await user.click(screen.getByRole("button", { name: /salvar alterações/i }));

    expect(onSubmit).toHaveBeenCalledTimes(1);
  });
});
