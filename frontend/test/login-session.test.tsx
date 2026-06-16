import { act, render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";
import Login from "@/app/page";

const routerMock = vi.hoisted(() => ({
  push: vi.fn(),
  replace: vi.fn(),
}));

const toastMock = vi.hoisted(() => ({
  showToast: vi.fn(),
}));

const authMock = vi.hoisted(() => ({
  login: vi.fn(),
}));

vi.mock("next/navigation", () => ({
  useRouter: () => routerMock,
}));

vi.mock("@/components/ToastProvider", () => ({
  useToast: () => toastMock,
}));

vi.mock("@/services/auth", () => authMock);

describe("Login page", () => {
  beforeEach(() => {
    authMock.login.mockReset();
  });

  it("exibe mensagem quando a sessão chega expirada pela URL", async () => {
    window.history.pushState({}, "", "/?session=expired");

    render(<Login />);

    expect(await screen.findByText("Sua sessão expirou. Faça login novamente.")).toBeInTheDocument();
    expect(routerMock.replace).toHaveBeenCalledWith("/");
  });

  it("reage ao evento global de sessão expirada", async () => {
    render(<Login />);

    act(() => {
      window.dispatchEvent(new CustomEvent("session-expired", { detail: { reason: "missing" } }));
    });

    expect(await screen.findByText("Faça login para continuar.")).toBeInTheDocument();
  });

  it("mostra erro quando credenciais são rejeitadas", async () => {
    authMock.login.mockRejectedValueOnce(new Error("invalid credentials"));
    const user = userEvent.setup();
    render(<Login />);

    await user.type(screen.getByLabelText(/email/i), "ana@example.com");
    await user.type(screen.getByLabelText(/senha/i), "senha-incorreta");
    await user.click(screen.getByRole("button", { name: /entrar/i }));

    expect(await screen.findByText("Email ou senha inválidos.")).toBeInTheDocument();
    expect(toastMock.showToast).toHaveBeenCalledWith("Email ou senha inválidos.", "error");
  });
});
