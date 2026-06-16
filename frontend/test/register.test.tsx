import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { beforeEach, describe, expect, it, vi } from "vitest";
import Register from "@/app/register/page";

const routerMock = vi.hoisted(() => ({
  push: vi.fn(),
  replace: vi.fn(),
}));

const toastMock = vi.hoisted(() => ({
  showToast: vi.fn(),
}));

const authMock = vi.hoisted(() => ({
  register: vi.fn(),
}));

vi.mock("next/navigation", () => ({
  useRouter: () => routerMock,
}));

vi.mock("@/components/ToastProvider", () => ({
  useToast: () => toastMock,
}));

vi.mock("@/services/auth", () => authMock);

describe("Register page", () => {
  beforeEach(() => {
    authMock.register.mockReset();
  });

  it("bloqueia cadastro inválido e mostra validações dos campos", async () => {
    const user = userEvent.setup();
    render(<Register />);

    await user.click(screen.getByRole("button", { name: /cadastrar/i }));

    expect(await screen.findByText("Informe seu nome.")).toBeInTheDocument();
    expect(screen.getByText("CPF deve ter 11 dígitos numéricos.")).toBeInTheDocument();
    expect(screen.getByText("Informe sua data de nascimento.")).toBeInTheDocument();
    expect(screen.getByText("Informe seu email.")).toBeInTheDocument();
    expect(screen.getByText("Informe sua senha.")).toBeInTheDocument();
    expect(screen.getByText("Informe seu endereço.")).toBeInTheDocument();
    expect(screen.getByText("Informe seu telefone.")).toBeInTheDocument();
    expect(authMock.register).not.toHaveBeenCalled();
    expect(toastMock.showToast).toHaveBeenCalledWith("Corrija os campos destacados.", "error");
  });

  it("envia cadastro válido e redireciona para login", async () => {
    authMock.register.mockResolvedValueOnce({ nome: "Ana Silva" });
    const user = userEvent.setup();
    render(<Register />);

    await user.type(screen.getByLabelText(/nome/i), "Ana Silva");
    await user.type(screen.getByLabelText(/cpf/i), "12345678901");
    await user.type(screen.getByLabelText(/data de nascimento/i), "1995-05-10");
    await user.type(screen.getByLabelText(/email/i), "ana@example.com");
    await user.type(screen.getByLabelText(/senha/i), "senha-segura");
    await user.type(screen.getByLabelText(/endereço/i), "Rua Central, 100");
    await user.type(screen.getByLabelText(/telefone/i), "(81) 98888-7777");
    await user.click(screen.getByRole("button", { name: /cadastrar/i }));

    await waitFor(() => {
      expect(authMock.register).toHaveBeenCalledWith({
        nome: "Ana Silva",
        CPF: "12345678901",
        dataNascimento: "1995-05-10",
        email: "ana@example.com",
        senha: "senha-segura",
        endereco: "Rua Central, 100",
        telefone: "(81) 98888-7777",
      });
    });
    expect(routerMock.push).toHaveBeenCalledWith("/");
  });
});
