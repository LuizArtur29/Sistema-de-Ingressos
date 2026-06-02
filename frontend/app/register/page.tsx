"use client";

import { useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { isAxiosError } from "axios";
import { register } from "@/services/auth";
import { ApiProblemDetail, RegisterRequest } from "@/services/types";
import { useToast } from "@/components/ToastProvider";
import styles from "./page.module.css";

type FieldErrors = Partial<Record<keyof RegisterRequest, string>>;

export default function Register() {
    const router = useRouter();
    const { showToast } = useToast();

    const [form, setForm] = useState<RegisterRequest>({
        nome: "",
        CPF: "",
        dataNascimento: "",
        email: "",
        senha: "",
        endereco: "",
        telefone: "",
    });

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [fieldErrors, setFieldErrors] = useState<FieldErrors>({});

    const updateField = (field: keyof RegisterRequest, value: string) => {
        setForm((prev) => ({ ...prev, [field]: value }));
        setFieldErrors((prev) => ({ ...prev, [field]: undefined }));
        setError(null);
    };

    const validate = (): boolean => {
        const errors: FieldErrors = {};

        if (!form.nome.trim()) errors.nome = "Informe seu nome.";
        if (!form.CPF.trim()) errors.CPF = "Informe seu CPF.";
        if (!/^\d{11}$/.test(form.CPF)) errors.CPF = "CPF deve ter 11 dígitos numéricos.";
        if (!form.dataNascimento) errors.dataNascimento = "Informe sua data de nascimento.";
        if (form.dataNascimento) {
            const date = new Date(form.dataNascimento);
            const today = new Date();
            if (isNaN(date.getTime()) || date >= today) {
                errors.dataNascimento = "Data de nascimento inválida.";
            }
        }
        if (!form.email.trim()) errors.email = "Informe seu email.";
        if (form.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
            errors.email = "Email inválido.";
        }
        if (!form.senha.trim()) errors.senha = "Informe sua senha.";
        if (!form.endereco.trim()) errors.endereco = "Informe seu endereço.";
        if (!form.telefone.trim()) errors.telefone = "Informe seu telefone.";
        if (
            form.telefone &&
            !/^\(?([0-9]{2})\)?[-. ]?([0-9]{4,5})[-. ]?([0-9]{4})$/.test(form.telefone)
        ) {
            errors.telefone = "Telefone inválido. Use (XX) XXXXX-XXXX.";
        }

        setFieldErrors(errors);
        return Object.keys(errors).length === 0;
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);

        if (!validate()) {
            showToast("Corrija os campos destacados.", "error");
            return;
        }

        setLoading(true);
        try {
            await register(form);
            showToast("Cadastro realizado com sucesso.", "success");
            router.push("/");
        } catch (err: unknown) {
            const data = isAxiosError<ApiProblemDetail>(err) ? err.response?.data : undefined;
            if (data?.errors?.length) {
                const apiErrors: FieldErrors = {};
                data.errors.forEach((item) => {
                    const key = item.field as keyof RegisterRequest;
                    apiErrors[key] = item.message;
                });
                setFieldErrors(apiErrors);
                setError("Corrija os erros do formulário.");
            } else {
                setError(data?.detail || "Erro ao cadastrar. Tente novamente.");
            }
            showToast("Erro ao cadastrar.", "error");
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
                <p className={styles.subtitle}>Crie sua conta para continuar</p>
            </div>

            <div className={styles.card}>
                <h2 className={styles.cardTitle}>Cadastro de usuário</h2>
                <p className={styles.cardSubtitle}>Preencha seus dados para criar a conta.</p>

                <form onSubmit={handleSubmit}>
                    <div className={styles.formGroup}>
                        <label className={styles.label}>Nome</label>
                        <div className={styles.inputWrapper}>
                            <input
                                type="text"
                                className={styles.input}
                                placeholder="Seu nome completo"
                                value={form.nome}
                                onChange={(e) => updateField("nome", e.target.value)}
                            />
                        </div>
                        {fieldErrors.nome && <p className={styles.errorText}>{fieldErrors.nome}</p>}
                    </div>

                    <div className={styles.formGroup}>
                        <label className={styles.label}>CPF</label>
                        <div className={styles.inputWrapper}>
                            <input
                                type="text"
                                className={styles.input}
                                placeholder="Somente números"
                                value={form.CPF}
                                onChange={(e) => updateField("CPF", e.target.value)}
                            />
                        </div>
                        {fieldErrors.CPF && <p className={styles.errorText}>{fieldErrors.CPF}</p>}
                    </div>

                    <div className={styles.formGroup}>
                        <label className={styles.label}>Data de nascimento</label>
                        <div className={styles.inputWrapper}>
                            <input
                                type="date"
                                className={styles.input}
                                value={form.dataNascimento}
                                onChange={(e) => updateField("dataNascimento", e.target.value)}
                            />
                        </div>
                        {fieldErrors.dataNascimento && (
                            <p className={styles.errorText}>{fieldErrors.dataNascimento}</p>
                        )}
                    </div>

                    <div className={styles.formGroup}>
                        <label className={styles.label}>Email</label>
                        <div className={styles.inputWrapper}>
                            <input
                                type="email"
                                className={styles.input}
                                placeholder="seu@email.com"
                                value={form.email}
                                onChange={(e) => updateField("email", e.target.value)}
                            />
                        </div>
                        {fieldErrors.email && <p className={styles.errorText}>{fieldErrors.email}</p>}
                    </div>

                    <div className={styles.formGroup}>
                        <label className={styles.label}>Senha</label>
                        <div className={styles.inputWrapper}>
                            <input
                                type="password"
                                className={styles.input}
                                placeholder="••••••••"
                                value={form.senha}
                                onChange={(e) => updateField("senha", e.target.value)}
                            />
                        </div>
                        {fieldErrors.senha && <p className={styles.errorText}>{fieldErrors.senha}</p>}
                    </div>

                    <div className={styles.formGroup}>
                        <label className={styles.label}>Endereço</label>
                        <div className={styles.inputWrapper}>
                            <input
                                type="text"
                                className={styles.input}
                                placeholder="Rua, número, bairro"
                                value={form.endereco}
                                onChange={(e) => updateField("endereco", e.target.value)}
                            />
                        </div>
                        {fieldErrors.endereco && <p className={styles.errorText}>{fieldErrors.endereco}</p>}
                    </div>

                    <div className={styles.formGroup}>
                        <label className={styles.label}>Telefone</label>
                        <div className={styles.inputWrapper}>
                            <input
                                type="text"
                                className={styles.input}
                                placeholder="(11) 98888-7777"
                                value={form.telefone}
                                onChange={(e) => updateField("telefone", e.target.value)}
                            />
                        </div>
                        {fieldErrors.telefone && <p className={styles.errorText}>{fieldErrors.telefone}</p>}
                    </div>

                    {error && <p className={styles.errorText}>{error}</p>}

                    <button type="submit" className={styles.button} disabled={loading}>
                        {loading ? "Cadastrando..." : "Cadastrar"}
                    </button>

                    <div className={styles.bottomLink}>
                        Já tem conta? <Link href="/">Entrar</Link>
                    </div>
                </form>
            </div>
        </div>
    );
}
