import { FormEvent } from "react";
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
    <div className={styles.formCard}>
      <form onSubmit={onSubmit} noValidate aria-describedby={error ? "event-form-error" : undefined}>
        <div className={styles.section}>
          <h2 className={styles.sectionTitle}>Informações Básicas</h2>

          <div className={styles.formGroup}>
            <label className={styles.label} htmlFor="event-name">
              Nome do evento <span className={styles.required}>*</span>
            </label>
            <input
              id="event-name"
              type="text"
              className={styles.input}
              placeholder="Ex.: Festival de Música 2026"
              value={form.nome}
              onChange={(e) => onFieldChange("nome", e.target.value)}
              disabled={!isAdmin}
              required
            />
            {fieldErrors.nome && <p className={styles.errorText}>{fieldErrors.nome}</p>}
          </div>

          <div className={styles.formGroup}>
            <label className={styles.label} htmlFor="event-description">
              Descrição <span className={styles.required}>*</span>
            </label>
            <textarea
              id="event-description"
              className={styles.textarea}
              placeholder="Descreva o evento, atrações, programação..."
              value={form.descricao}
              onChange={(e) => onFieldChange("descricao", e.target.value)}
              disabled={!isAdmin}
              required
            />
            {fieldErrors.descricao && <p className={styles.errorText}>{fieldErrors.descricao}</p>}
          </div>
        </div>

        <div className={styles.section}>
          <h2 className={styles.sectionTitle}>Data e Local</h2>

          <div className={styles.row}>
            <div className={styles.col}>
              <label className={styles.label} htmlFor="event-start-date">
                Data de início <span className={styles.required}>*</span>
              </label>
              <input
                id="event-start-date"
                type="date"
                className={styles.input}
                value={form.dataInicio}
                onChange={(e) => onFieldChange("dataInicio", e.target.value)}
                disabled={!isAdmin}
                required
              />
              {fieldErrors.dataInicio && <p className={styles.errorText}>{fieldErrors.dataInicio}</p>}
            </div>
            <div className={styles.col}>
              <label className={styles.label} htmlFor="event-end-date">
                Data de término <span className={styles.required}>*</span>
              </label>
              <input
                id="event-end-date"
                type="date"
                className={styles.input}
                value={form.dataFim}
                onChange={(e) => onFieldChange("dataFim", e.target.value)}
                disabled={!isAdmin}
                required
              />
              {fieldErrors.dataFim && <p className={styles.errorText}>{fieldErrors.dataFim}</p>}
            </div>
          </div>

          <div className={styles.formGroup}>
            <label className={styles.label} htmlFor="event-location">
              Local <span className={styles.required}>*</span>
            </label>
            <input
              id="event-location"
              type="text"
              className={styles.input}
              placeholder="Ex.: Parque Ibirapuera, São Paulo - SP"
              value={form.local}
              onChange={(e) => onFieldChange("local", e.target.value)}
              disabled={!isAdmin}
              required
            />
            {fieldErrors.local && <p className={styles.errorText}>{fieldErrors.local}</p>}
          </div>
        </div>

        <div className={styles.section}>
          <h2 className={styles.sectionTitle}>Detalhes</h2>

          <div className={styles.row}>
            <div className={styles.col}>
              <label className={styles.label} htmlFor="event-capacity">
                Capacidade total <span className={styles.required}>*</span>
              </label>
              <input
                id="event-capacity"
                type="number"
                min="1"
                step="1"
                className={styles.input}
                placeholder="Ex.: 500"
                value={form.capacidadeTotal}
                onChange={(e) => onFieldChange("capacidadeTotal", e.target.value)}
                disabled={!isAdmin}
                required
              />
              {fieldErrors.capacidadeTotal && (
                <p className={styles.errorText}>{fieldErrors.capacidadeTotal}</p>
              )}
            </div>
            <div className={styles.col}>
              <label className={styles.label} htmlFor="event-status">
                Status <span className={styles.required}>*</span>
              </label>
              <select
                id="event-status"
                className={styles.select}
                value={form.status}
                onChange={(e) => onFieldChange("status", e.target.value as EventoStatus)}
                disabled={!isAdmin}
                required
              >
                <option value="ATIVO">Ativo</option>
                <option value="CANCELADO">Cancelado</option>
                <option value="FINALIZADO">Finalizado</option>
              </select>
              {fieldErrors.status && <p className={styles.errorText}>{fieldErrors.status}</p>}
            </div>
          </div>
        </div>

        {error && <p id="event-form-error" className={styles.errorText}>{error}</p>}

        <div className={styles.actions}>
          <button type="button" className={styles.btnCancel} onClick={onCancel} disabled={saving || deleting}>
            Cancelar
          </button>
          {isAdmin && (
            <button type="submit" className={styles.btnSave} disabled={saving || deleting}>
              {saving ? "Salvando..." : "Salvar Alterações"}
            </button>
          )}
        </div>
      </form>
    </div>
  );
}
