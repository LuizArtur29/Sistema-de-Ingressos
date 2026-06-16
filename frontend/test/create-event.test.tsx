import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";
import CreateEvent from "@/app/dashboard/create/page";

const routerMock = vi.hoisted(() => ({
  push: vi.fn(),
  replace: vi.fn(),
}));

const toastMock = vi.hoisted(() => ({
  showToast: vi.fn(),
}));

const eventosMock = vi.hoisted(() => ({
  criarEvento: vi.fn(),
}));

vi.mock("next/navigation", () => ({
  useRouter: () => routerMock,
}));

vi.mock("@/components/ToastProvider", () => ({
  useToast: () => toastMock,
}));

vi.mock("@/components/AdminGuard", () => ({
  default: ({ children }: { children: React.ReactNode }) => <>{children}</>,
}));

vi.mock("@/services/eventos", () => eventosMock);

describe("Create event page", () => {
  beforeEach(() => {
    eventosMock.criarEvento.mockReset();
  });

  it("bloqueia submit inválido e mostra validações de criação", async () => {
    const user = userEvent.setup();
    render(<CreateEvent />);

    await user.click(screen.getByRole("button", { name: /salvar evento/i }));

    expect(await screen.findByText("Informe o nome do evento.")).toBeInTheDocument();
    expect(screen.getByText("Informe a descrição do evento.")).toBeInTheDocument();
    expect(screen.getByText("Informe a data de início.")).toBeInTheDocument();
    expect(screen.getByText("Informe a data de término.")).toBeInTheDocument();
    expect(screen.getByText("Informe o local do evento.")).toBeInTheDocument();
    expect(screen.getByText("Informe a capacidade total.")).toBeInTheDocument();
    expect(eventosMock.criarEvento).not.toHaveBeenCalled();
    expect(toastMock.showToast).toHaveBeenCalledWith("Corrija os campos destacados.", "error");
  });

  it("envia payload normalizado quando formulário é válido", async () => {
    eventosMock.criarEvento.mockResolvedValueOnce({ id: 1 });
    const user = userEvent.setup();
    render(<CreateEvent />);

    await user.type(screen.getByLabelText(/nome do evento/i), "  Festival Recife  ");
    await user.type(screen.getByLabelText(/descrição/i), "  Shows e gastronomia  ");
    await user.type(screen.getByLabelText(/data de início/i), "2026-08-20");
    await user.type(screen.getByLabelText(/data de término/i), "2026-08-21");
    await user.type(screen.getByLabelText(/local/i), "  Marco Zero  ");
    await user.type(screen.getByLabelText(/capacidade total/i), "5000");
    await user.selectOptions(screen.getByLabelText(/status/i), "ATIVO");
    await user.click(screen.getByRole("button", { name: /salvar evento/i }));

    await waitFor(() => {
      expect(eventosMock.criarEvento).toHaveBeenCalledWith({
        nome: "Festival Recife",
        descricao: "Shows e gastronomia",
        dataInicio: "2026-08-20",
        dataFim: "2026-08-21",
        local: "Marco Zero",
        capacidadeTotal: 5000,
        status: "ATIVO",
      });
    });
    expect(routerMock.push).toHaveBeenCalledWith("/dashboard");
  });
});
