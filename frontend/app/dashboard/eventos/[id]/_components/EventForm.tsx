import { FormEvent } from "react";
import { Button, Card, FormActions, SelectField, TextareaField, TextField } from "@/components/ui";
import { EventoStatus } from "@/services/types";
import { EventFieldErrors, EventFormState } from "../_utils/eventDetailsTypes";
import styles from "../page.module.css";

type EventFormProps = {
  form: EventFormState;
  fieldErrors: EventFieldErrors;
  error: string | null;
  isAdmin: boolean;
  saving: boolean;
  deleting: boolean;
  onCancel: () => void;
  onSubmit: (event: FormEvent) => void;
  onFieldChange: <K extends keyof EventFormState>(field: K, value: EventFormState[K]) => void;
};

export default function EventForm({
  form,
  fieldErrors,
  error,
  isAdmin,
  saving,
  deleting,
  onCancel,
  onSubmit,
  onFieldChange,
}: EventFormProps) {
  return (
    <Card>
      <form onSubmit={onSubmit} noValidate aria-describedby={error ? "event-form-error" : undefined}>
        <div className={styles.section}>
          <h2 className={styles.sectionTitle}>Informações Básicas</h2>

          <TextField
            id="event-name"
            label="Nome do evento"
            type="text"
            placeholder="Ex.: Festival de Música 2026"
            value={form.nome}
            onChange={(e) => onFieldChange("nome", e.target.value)}
            disabled={!isAdmin}
            required
            error={fieldErrors.nome}
          />

          <TextareaField
            id="event-description"
            label="Descrição"
            placeholder="Descreva o evento, atrações, programação..."
            value={form.descricao}
            onChange={(e) => onFieldChange("descricao", e.target.value)}
            disabled={!isAdmin}
            required
            error={fieldErrors.descricao}
          />
        </div>

        <div className={styles.section}>
          <h2 className={styles.sectionTitle}>Data e Local</h2>

          <div className={styles.row}>
            <div className={styles.col}>
              <TextField
                id="event-start-date"
                label="Data de início"
                type="date"
                value={form.dataInicio}
                onChange={(e) => onFieldChange("dataInicio", e.target.value)}
                disabled={!isAdmin}
                required
                error={fieldErrors.dataInicio}
              />
            </div>
            <div className={styles.col}>
              <TextField
                id="event-end-date"
                label="Data de término"
                type="date"
                value={form.dataFim}
                onChange={(e) => onFieldChange("dataFim", e.target.value)}
                disabled={!isAdmin}
                required
                error={fieldErrors.dataFim}
              />
            </div>
          </div>

          <TextField
            id="event-location"
            label="Local"
            type="text"
            placeholder="Ex.: Parque Ibirapuera, São Paulo - SP"
            value={form.local}
            onChange={(e) => onFieldChange("local", e.target.value)}
            disabled={!isAdmin}
            required
            error={fieldErrors.local}
          />
        </div>

        <div className={styles.section}>
          <h2 className={styles.sectionTitle}>Detalhes</h2>

          <div className={styles.row}>
            <div className={styles.col}>
              <TextField
                id="event-capacity"
                label="Capacidade total"
                type="number"
                min="1"
                step="1"
                placeholder="Ex.: 500"
                value={form.capacidadeTotal}
                onChange={(e) => onFieldChange("capacidadeTotal", e.target.value)}
                disabled={!isAdmin}
                required
                error={fieldErrors.capacidadeTotal}
              />
            </div>
            <div className={styles.col}>
              <SelectField
                id="event-status"
                label="Status"
                value={form.status}
                onChange={(e) => onFieldChange("status", e.target.value as EventoStatus)}
                disabled={!isAdmin}
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

        {error && <p id="event-form-error" className={styles.errorText}>{error}</p>}

        <FormActions>
          <Button type="button" variant="secondary" onClick={onCancel} disabled={saving || deleting}>
            Cancelar
          </Button>
          {isAdmin && (
            <Button type="submit" disabled={saving || deleting}>
              {saving ? "Salvando..." : "Salvar Alterações"}
            </Button>
          )}
        </FormActions>
      </form>
    </Card>
  );
}
