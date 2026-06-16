import { render, screen, waitFor } from "@testing-library/react";
import { beforeEach, describe, expect, it, vi } from "vitest";
import Dashboard from "@/app/dashboard/page";
import { EventoResponse } from "@/services/types";

const toastMock = vi.hoisted(() => ({
  showToast: vi.fn(),
}));

const authMock = vi.hoisted(() => ({
  isAdmin: false,
}));

const eventosMock = vi.hoisted(() => ({
  listarEventos: vi.fn(),
  listarMeusEventos: vi.fn(),
}));

vi.mock("@/components/ToastProvider", () => ({
  useToast: () => toastMock,
}));

vi.mock("@/hooks/useAuthUser", () => ({
  usePermissions: () => ({
    isAdmin: authMock.isAdmin,
    loading: false,
    role: authMock.isAdmin ? "ADMINISTRADOR" : "USUARIO",
  }),
}));

vi.mock("@/services/eventos", () => eventosMock);

const eventoAtivo: EventoResponse = {
  id: 1,
  nome: "Festival Recife",
  descricao: "Shows e gastronomia",
  dataInicio: "2026-08-20",
  dataFim: "2026-08-21",
  local: "Marco Zero",
  capacidadeTotal: 5000,
  status: "ATIVO",
  sessoes: [
    {
      idSessao: 10,
      nomeSessao: "Noite principal",
      dataHoraSessao: "2026-08-20T20:00:00",
      statusSessao: "AGENDADA",
      capacidade: 5000,
    },
  ],
};

describe("Dashboard page", () => {
  beforeEach(() => {
    authMock.isAdmin = false;
    eventosMock.listarEventos.mockReset();
    eventosMock.listarMeusEventos.mockReset();
  });

  it("renderiza estado de loading", () => {
    eventosMock.listarEventos.mockReturnValue(new Promise(() => undefined));
    eventosMock.listarMeusEventos.mockReturnValue(new Promise(() => undefined));

    render(<Dashboard />);

    expect(screen.getByText("Carregando eventos...")).toBeInTheDocument();
  });

  it("renderiza estado de erro", async () => {
    eventosMock.listarEventos.mockRejectedValueOnce(new Error("falha"));
    eventosMock.listarMeusEventos.mockResolvedValueOnce([]);

    render(<Dashboard />);

    expect(await screen.findByText("Não foi possível carregar os eventos.")).toBeInTheDocument();
    expect(toastMock.showToast).toHaveBeenCalledWith("Erro ao carregar eventos.", "error");
  });

  it("renderiza estado vazio", async () => {
    eventosMock.listarEventos.mockResolvedValueOnce([]);
    eventosMock.listarMeusEventos.mockResolvedValueOnce([]);

    render(<Dashboard />);

    expect(await screen.findByText("Nenhum evento encontrado")).toBeInTheDocument();
    expect(screen.getByText("0 eventos encontrados")).toBeInTheDocument();
  });

  it("renderiza métricas e cards quando há dados", async () => {
    eventosMock.listarEventos.mockResolvedValueOnce([eventoAtivo]);
    eventosMock.listarMeusEventos.mockResolvedValueOnce([]);

    render(<Dashboard />);

    expect(await screen.findByText("Festival Recife")).toBeInTheDocument();
    expect(screen.getByText("1 eventos encontrados")).toBeInTheDocument();
    expect(screen.getByText("Eventos ativos")).toBeInTheDocument();
    expect(screen.getByText("5.000")).toBeInTheDocument();
    await waitFor(() => {
      expect(eventosMock.listarEventos).toHaveBeenCalledTimes(1);
    });
  });
});
