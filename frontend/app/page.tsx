"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { login } from "@/services/auth";
import styles from "./page.module.css";
import { useToast } from "@/components/ToastProvider";

export default function Login() {
  const router = useRouter();

  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const { showToast } = useToast();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const handler = () => {
      setError("Sua sessão expirou. Faça login novamente.");
    };
    window.addEventListener("session-expired", handler);
    return () => window.removeEventListener("session-expired", handler);
  }, []);

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      const res = await login({ email, senha });
      sessionStorage.setItem("token", res.jwt);
      showToast("Login realizado com sucesso.", "success");
      router.push("/dashboard");
    } catch {
      showToast("Email ou senha inválidos.", "error");
    } finally {
      setLoading(false);
    }
  };

  return (
      <div className={styles.container}>
        <div className={styles.header}>
          <div className={styles.logoIcon}>
            <svg
                xmlns="http://www.w3.org/2000/svg"
                width="24"
                height="24"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
            >
              <path d="M4 7V5c0-1.1.9-2 2-2h12c1.1 0 2 .9 2 2v2" />
              <path d="M20 17v2c0 1.1-.9 2-2 2H6c-1.1 0-2-.9-2-2v-2" />
              <path d="M4 12c1.1 0 2-.9 2-2 0-1.1-.9-2-2-2" />
              <path d="M20 12c-1.1 0-2-.9-2-2 0-1.1.9-2 2-2" />
              <circle cx="9" cy="12" r="1" />
              <circle cx="15" cy="12" r="1" />
              <line x1="12" y1="9" x2="12" y2="15" />
            </svg>
          </div>
          <h1 className={styles.title}>TicketHub</h1>
          <p className={styles.subtitle}>Plataforma de venda de ingressos</p>
        </div>

        <div className={styles.card}>
          <h2 className={styles.cardTitle}>Entrar na sua conta</h2>
          <p className={styles.cardSubtitle}>Bem-vindo de volta! Faça login para continuar.</p>

          <form onSubmit={handleLogin}>
            <div className={styles.formGroup}>
              <label className={styles.label}>Email</label>
              <div className={styles.inputWrapper}>
                <input
                    type="email"
                    className={styles.input}
                    placeholder="seu@email.com"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                />
              </div>
            </div>

            <div className={styles.formGroup}>
              <label className={styles.label}>Senha</label>
              <div className={styles.inputWrapper}>
                <input
                    type="password"
                    className={styles.input}
                    placeholder="••••••••"
                    value={senha}
                    onChange={(e) => setSenha(e.target.value)}
                />
                <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="20"
                    height="20"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth="2"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    className={styles.eyeIcon}
                >
                  <path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7Z" />
                  <circle cx="12" cy="12" r="3" />
                </svg>
              </div>
              <Link href="#" className={styles.forgotPassword}>
                Esqueci minha senha
              </Link>
            </div>

            {error && <p className={styles.errorText}>{error}</p>}

            <button type="submit" className={styles.button} disabled={loading}>
              {loading ? "Entrando..." : "Entrar"}
            </button>
          </form>
        </div>

        <div className={styles.demoBox}>
          Acesso demo: demo@tickethub.com / demo123
        </div>
      </div>
  );
}