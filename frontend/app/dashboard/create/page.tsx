"use client";

import { useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { isAxiosError } from "axios";
import AdminGuard from "@/components/AdminGuard";
import { useToast } from "@/components/ToastProvider";
import { Button, Card, FormActions, PageHeader, SelectField, TextareaField, TextField } from "@/components/ui";
import { getForbiddenMessage, isForbiddenError } from "@/lib/apiErrors";
import { criarEvento } from "@/services/eventos";
import { ApiProblemDetail, EventoCreateRequest, EventoStatus } from "@/services/types";
import { EventoFieldErrors, validateEventoForm } from "@/lib/validation/evento";
import styles from "./page.module.css";

type FormState = Omit<EventoCreateRequest, "capacidadeTotal"> & {
  capacidadeTotal: string;
};

type FieldErrors = EventoFieldErrors<FormState>;

const initialForm: FormState = {
  nome: "",
  descricao: "",
  dataInicio: "",
  dataFim: "",
  local: "",
  capacidadeTotal: "",
  status: "ATIVO",
};

export default function CreateEvent() {
  const router = useRouter();
  const { showToast } = useToast();

  const [form, setForm] = useState<FormState>(initialForm);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [fieldErrors, setFieldErrors] = useState<FieldErrors>({});

  const updateField = <K extends keyof FormState>(field: K, value: FormState[K]) => {
    setForm((prev) => ({ ...prev, [field]: value }));
    setFieldErrors((prev) => ({ ...prev, [field]: undefined }));
    setError(null);
  };

  const validate = (): boolean => {
    const errors = validateEventoForm(form);
    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    if (loading) return;

    setError(null);

    if (!validate()) {
      showToast("Corrija os campos destacados.", "error");
      return;
    }

    setLoading(true);

    try {
      const payload: EventoCreateRequest = {
        ...form,
        nome: form.nome.trim(),
        descricao: form.descricao.trim(),
        local: form.local.trim(),
        capacidadeTotal: Number(form.capacidadeTotal),
      };
      await criarEvento(payload);
      showToast("Evento criado com sucesso.", "success");
      router.push("/dashboard");
    } catch (err: unknown) {
      if (isForbiddenError(err)) {
        const message = getForbiddenMessage();
        setError(message);
        showToast(message, "error");
        return;
      }

      const data = isAxiosError<ApiProblemDetail>(err) ? err.response?.data : undefined;

      if (data?.errors?.length) {
        const apiErrors: FieldErrors = {};
        const unmappedErrors: string[] = [];

        data.errors.forEach((item) => {
          if (item.field in initialForm) {
            apiErrors[item.field as keyof FormState] = item.message;
            return;
          }
          unmappedErrors.push(item.message);
        });

        setFieldErrors(apiErrors);
        setError(unmappedErrors[0] || "Corrija os erros do formulário.");
      } else {
        setError(data?.detail || "Não foi possível criar o evento. Tente novamente.");
      }
      showToast("Falha ao criar evento.", "error");
    } finally {
      setLoading(false);
    }
  };

  return (
    <AdminGuard>
      <div className={styles.container}>
        <div className={styles.breadcrumbs}>
          <Link href="/dashboard">Eventos</Link> &gt; <span>Criar Evento</span>
        </div>

        <PageHeader
          eyebrow="Gerenciamento"
          title="Criar novo evento"
          subtitle="Cadastre as informações principais e depois configure sessões e tipos de ingresso."
        />

        <Card>
          <form onSubmit={handleSave} noValidate>
            <div className={styles.section}>
              <h2 className={styles.sectionTitle}>Informações Básicas</h2>

              <TextField
                id="create-event-name"
                label="Nome do evento"
                type="text"
                placeholder="Ex.: Festival de Música 2026"
                value={form.nome}
                onChange={(e) => updateField("nome", e.target.value)}
                required
                error={fieldErrors.nome}
              />

              <TextareaField
                id="create-event-description"
                label="Descrição"
                placeholder="Descreva o evento, atrações, programação..."
                value={form.descricao}
                onChange={(e) => updateField("descricao", e.target.value)}
                required
                error={fieldErrors.descricao}
              />
            </div>

            <div className={styles.section}>
              <h2 className={styles.sectionTitle}>Data e Local</h2>

              <div className={styles.row}>
                <div className={styles.col}>
                  <TextField
                    id="create-event-start-date"
                    label="Data de início"
                    type="date"
                    value={form.dataInicio}
                    onChange={(e) => updateField("dataInicio", e.target.value)}
                    required
                    error={fieldErrors.dataInicio}
                  />
                </div>
                <div className={styles.col}>
                  <TextField
                    id="create-event-end-date"
                    label="Data de término"
                    type="date"
                    value={form.dataFim}
                    onChange={(e) => updateField("dataFim", e.target.value)}
                    required
                    error={fieldErrors.dataFim}
                  />
                </div>
              </div>

              <TextField
                id="create-event-location"
                label="Local"
                type="text"
                placeholder="Ex.: Parque Ibirapuera, São Paulo - SP"
                value={form.local}
                onChange={(e) => updateField("local", e.target.value)}
                required
                error={fieldErrors.local}
              />
            </div>

            <div className={styles.section}>
              <h2 className={styles.sectionTitle}>Detalhes</h2>

              <div className={styles.row}>
                <div className={styles.col}>
                  <TextField
                    id="create-event-capacity"
                    label="Capacidade total"
                    type="number"
                    placeholder="Ex.: 500"
                    value={form.capacidadeTotal}
                    onChange={(e) => updateField("capacidadeTotal", e.target.value)}
                    min="1"
                    step="1"
                    required
                    error={fieldErrors.capacidadeTotal}
                  />
                </div>
                <div className={styles.col}>
                  <SelectField
                    id="create-event-status"
                    label="Status"
                    value={form.status}
                    onChange={(e) => updateField("status", e.target.value as EventoStatus)}
                    required
                    error={fieldErrors.status}
                  >
                    <option value="ATIVO">Ativo</option>
                    <option value="CANCELADO">Cancelado</option>
                    <option value="FINALIZADO">Finalizado</option>
                  </SelectField>
                </div>
              </div>
            </div>

            {error && <p className={styles.errorText}>{error}</p>}

            <FormActions>
              <Button
                type="button"
                variant="secondary"
                onClick={() => router.push("/dashboard")}
                disabled={loading}
              >
                Cancelar
              </Button>
              <Button type="submit" disabled={loading}>
                {loading ? "Salvando..." : "Salvar Evento"}
              </Button>
            </FormActions>
          </form>
        </Card>
      </div>
    </AdminGuard>
  );
}
