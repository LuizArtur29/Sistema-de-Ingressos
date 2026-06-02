"use client";

import { FormEvent, useEffect, useState } from "react";
import Link from "next/link";
import { useParams, useRouter } from "next/navigation";
import { useToast } from "@/components/ToastProvider";
import { atualizarEvento, buscarEventoPorId, excluirEvento } from "@/services/eventos";
import { Evento, EventoPayload } from "@/services/types";
import styles from "./page.module.css";

type FormState = EventoPayload;

const initialFormState: FormState = {
  nome: "",
  descricao: "",
  dataInicio: "",
  dataFim: "",
  local: "",
  capacidadeTotal: 0,
  status: "ATIVO",
};

const statusLabelMap: Record<Evento["status"], string> = {
  ATIVO: "Ativo",
  CANCELADO: "Cancelado",
  FINALIZADO: "Finalizado",
};

function normalizeDate(value?: string) {
  if (!value) return "";
  return value.slice(0, 10);
}

function toFormState(evento: Evento): FormState {
  return {
    nome: evento.nome,
    descricao: evento.descricao,
    dataInicio: normalizeDate(evento.dataInicio),
    dataFim: normalizeDate(evento.dataFim),
    local: evento.local,
    capacidadeTotal: evento.capacidadeTotal,
    status: evento.status,
  };
}

export default function EventDetails() {
  const router = useRouter();
  const params = useParams<{ id: string }>();
  const { showToast } = useToast();

  const eventId = Number(params.id);
  const [form, setForm] = useState<FormState>(initialFormState);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [deleting, setDeleting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!Number.isFinite(eventId)) {
      setError("Evento inválido.");
      setLoading(false);
      return;
    }

    const load = async () => {
      try {
        const evento = await buscarEventoPorId(eventId);
        setForm(toFormState(evento));
      } catch {
        setError("Não foi possível carregar os dados do evento.");
        showToast("Erro ao carregar evento.", "error");
      } finally {
        setLoading(false);
      }
    };

    load();
  }, [eventId, showToast]);

  const updateField = <K extends keyof FormState>(field: K, value: FormState[K]) => {
    setForm((current) => ({ ...current, [field]: value }));
  };

  const handleSave = async (e: FormEvent) => {
    e.preventDefault();
    setError(null);
    setSaving(true);

    try {
      await atualizarEvento(eventId, form);
      showToast("Evento atualizado com sucesso.", "success");
      router.push("/dashboard");
    } catch {
      setError("Não foi possível atualizar o evento.");
      showToast("Falha ao atualizar evento.", "error");
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async () => {
    const confirmed = window.confirm(
      "Tem certeza que deseja excluir este evento? Essa ação não pode ser desfeita."
    );

    if (!confirmed) return;

    setError(null);
    setDeleting(true);

    try {
      await excluirEvento(eventId);
      showToast("Evento excluído com sucesso.", "success");
      router.push("/dashboard");
    } catch {
      setError("Não foi possível excluir o evento.");
      showToast("Falha ao excluir evento.", "error");
    } finally {
      setDeleting(false);
    }
  };

  if (loading) {
    return <p className={styles.feedback}>Carregando evento...</p>;
  }

  if (error && !form.nome) {
    return (
      <div className={styles.container}>
        <div className={styles.breadcrumbs}>
          <Link href="/dashboard">Eventos</Link> &gt; <span>Detalhes</span>
        </div>
        <div className={styles.errorCard}>
          <p>{error}</p>
          <button className={styles.btnCancel} onClick={() => router.push("/dashboard")}>
            Voltar ao dashboard
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className={styles.container}>
      <div className={styles.breadcrumbs}>
        <Link href="/dashboard">Eventos</Link> &gt; <span>{form.nome}</span>
      </div>

      <div className={styles.header}>
        <div>
          <span className={`${styles.badge} ${styles[form.status.toLowerCase()]}`}>
            {statusLabelMap[form.status]}
          </span>
          <h1 className={styles.title}>Detalhes do Evento</h1>
          <p className={styles.subtitle}>Visualize, edite ou exclua este evento.</p>
        </div>
        <button
          type="button"
          className={styles.btnDelete}
          onClick={handleDelete}
          disabled={deleting || saving}
        >
          {deleting ? "Excluindo..." : "Excluir Evento"}
        </button>
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
                value={form.nome}
                onChange={(e) => updateField("nome", e.target.value)}
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
                value={form.descricao}
                onChange={(e) => updateField("descricao", e.target.value)}
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
                <input
                  type="date"
                  className={styles.input}
                  value={form.dataInicio}
                  onChange={(e) => updateField("dataInicio", e.target.value)}
                  required
                />
              </div>
              <div className={styles.col}>
                <label className={styles.label}>
                  Data de término <span className={styles.required}>*</span>
                </label>
                <input
                  type="date"
                  className={styles.input}
                  value={form.dataFim}
                  onChange={(e) => updateField("dataFim", e.target.value)}
                  required
                />
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
                value={form.local}
                onChange={(e) => updateField("local", e.target.value)}
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
                  min="1"
                  className={styles.input}
                  placeholder="Ex.: 500"
                  value={form.capacidadeTotal || ""}
                  onChange={(e) => updateField("capacidadeTotal", Number(e.target.value))}
                  required
                />
              </div>
              <div className={styles.col}>
                <label className={styles.label}>
                  Status <span className={styles.required}>*</span>
                </label>
                <select
                  className={styles.select}
                  value={form.status}
                  onChange={(e) => updateField("status", e.target.value as Evento["status"])}
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
              disabled={saving || deleting}
            >
              Cancelar
            </button>
            <button type="submit" className={styles.btnSave} disabled={saving || deleting}>
              {saving ? "Salvando..." : "Salvar Alterações"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
