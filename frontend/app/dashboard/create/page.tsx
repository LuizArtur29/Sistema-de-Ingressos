"use client";

import { useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useToast } from "@/components/ToastProvider";
import { criarEvento } from "@/services/eventos";
import styles from "./page.module.css";

export default function CreateEvent() {
  const router = useRouter();

  const [nome, setNome] = useState("");
  const [descricao, setDescricao] = useState("");
  const [dataInicio, setDataInicio] = useState("");
  const [dataFim, setDataFim] = useState("");
  const [local, setLocal] = useState("");
  const { showToast } = useToast();
  const [capacidadeTotal, setCapacidadeTotal] = useState(0);
  const [status, setStatus] = useState<"ATIVO" | "CANCELADO" | "FINALIZADO">("ATIVO");

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      await criarEvento({
        nome,
        descricao,
        dataInicio,
        dataFim,
        local,
        capacidadeTotal,
        status,
      });
      showToast("Evento criado com sucesso.", "success");
      router.push("/dashboard");
    } catch {
      setError("Não foi possível criar o evento.");
      showToast("Falha ao criar evento.", "error");
    } finally {
      setLoading(false);
    }
  };

  return (
      <div className={styles.container}>
        <div className={styles.breadcrumbs}>
          <Link href="/dashboard">Eventos</Link> &gt; <span>Criar Evento</span>
        </div>

        <div className={styles.header}>
          <h1 className={styles.title}>Criar Novo Evento</h1>
          <p className={styles.subtitle}>Preencha os campos abaixo para cadastrar um novo evento.</p>
        </div>

        <div className={styles.formCard}>
          <form onSubmit={handleSave}>
            <div className={styles.section}>
              <h2 className={styles.sectionTitle}>Informações Básicas</h2>

              <div className={styles.formGroup}>
                <label className={styles.label}>
                  Nome do evento <span className={styles.required}>*</span>
                </label>
                <input
                    type="text"
                    className={styles.input}
                    placeholder="Ex.: Festival de Música 2026"
                    value={nome}
                    onChange={(e) => setNome(e.target.value)}
                    required
                />
              </div>

              <div className={styles.formGroup}>
                <label className={styles.label}>
                  Descrição <span className={styles.required}>*</span>
                </label>
                <textarea
                    className={styles.textarea}
                    placeholder="Descreva o evento, atrações, programação..."
                    value={descricao}
                    onChange={(e) => setDescricao(e.target.value)}
                    required
                />
              </div>
            </div>

            <div className={styles.section}>
              <h2 className={styles.sectionTitle}>Data e Local</h2>

              <div className={styles.row}>
                <div className={styles.col}>
                  <label className={styles.label}>
                    Data de início <span className={styles.required}>*</span>
                  </label>
                  <div className={styles.inputIcon}>
                    <input
                        type="date"
                        className={styles.input}
                        value={dataInicio}
                        onChange={(e) => setDataInicio(e.target.value)}
                        required
                    />
                  </div>
                </div>
                <div className={styles.col}>
                  <label className={styles.label}>
                    Data de término <span className={styles.required}>*</span>
                  </label>
                  <div className={styles.inputIcon}>
                    <input
                        type="date"
                        className={styles.input}
                        value={dataFim}
                        onChange={(e) => setDataFim(e.target.value)}
                        required
                    />
                  </div>
                </div>
              </div>

              <div className={styles.formGroup}>
                <label className={styles.label}>
                  Local <span className={styles.required}>*</span>
                </label>
                <input
                    type="text"
                    className={styles.input}
                    placeholder="Ex.: Parque Ibirapuera, São Paulo - SP"
                    value={local}
                    onChange={(e) => setLocal(e.target.value)}
                    required
                />
              </div>
            </div>

            <div className={styles.section}>
              <h2 className={styles.sectionTitle}>Detalhes</h2>

              <div className={styles.row}>
                <div className={styles.col}>
                  <label className={styles.label}>
                    Capacidade total <span className={styles.required}>*</span>
                  </label>
                  <input
                      type="number"
                      className={styles.input}
                      placeholder="Ex.: 500"
                      value={capacidadeTotal || ""}
                      onChange={(e) => setCapacidadeTotal(Number(e.target.value))}
                      required
                  />
                </div>
                <div className={styles.col}>
                  <label className={styles.label}>
                    Status <span className={styles.required}>*</span>
                  </label>
                  <select
                      className={styles.select}
                      value={status}
                      onChange={(e) => setStatus(e.target.value as typeof status)}
                      required
                  >
                    <option value="ATIVO">Ativo</option>
                    <option value="CANCELADO">Cancelado</option>
                    <option value="FINALIZADO">Finalizado</option>
                  </select>
                </div>
              </div>
            </div>

            {error && <p className={styles.errorText}>{error}</p>}

            <div className={styles.actions}>
              <button
                  type="button"
                  className={styles.btnCancel}
                  onClick={() => router.push("/dashboard")}
              >
                Cancelar
              </button>
              <button type="submit" className={styles.btnSave} disabled={loading}>
                Salvar Evento
              </button>
            </div>
          </form>
        </div>
      </div>
  );
}