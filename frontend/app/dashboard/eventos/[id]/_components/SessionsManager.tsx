import { FormEvent } from "react";
import { SessaoEventoResponse } from "@/services/types";
import { toDateTimeLocal } from "../_utils/dateHelpers";
import { SESSION_STATUS_OPTIONS, SessionFieldErrors, SessionStatus } from "../_utils/eventDetailsTypes";
import styles from "../page.module.css";

type SessionsManagerProps = {
  sessoes: SessaoEventoResponse[];
  loadingSessoes: boolean;
  errorSessoes: string | null;
  isAdmin: boolean;
  form: {
    nome: string;
    dataHora: string;
    status: SessionStatus;
    capacidade: string;
    editando: SessaoEventoResponse | null;
    fieldErrors: SessionFieldErrors;
    formError: string | null;
  };
  onEditSession: (sessao: SessaoEventoResponse) => void;
  onRemoveSession: (sessao: SessaoEventoResponse) => void;
  onSubmitSession: (event: FormEvent) => void;
  onSessionNameChange: (value: string) => void;
  onSessionDateTimeChange: (value: string) => void;
  onSessionStatusChange: (value: SessionStatus) => void;
  onSessionCapacityChange: (value: string) => void;
  deletingSessionId: number | null;
};

export default function SessionsManager({
  sessoes,
  loadingSessoes,
  errorSessoes,
  isAdmin,
  form,
  onEditSession,
  onRemoveSession,
  onSubmitSession,
  onSessionNameChange,
  onSessionDateTimeChange,
  onSessionStatusChange,
  onSessionCapacityChange,
  deletingSessionId,
}: SessionsManagerProps) {
  return (
    <div className={styles.section}>
      <h2 className={styles.sectionTitle}>Sessões do Evento</h2>

      {loadingSessoes && <p className={styles.feedback}>Carregando sessões...</p>}
      {errorSessoes && <p className={styles.errorText}>{errorSessoes}</p>}

      {!loadingSessoes && !errorSessoes && sessoes.length === 0 && (
        <p className={styles.feedback}>Nenhuma sessão cadastrada.</p>
      )}

      {!loadingSessoes && sessoes.length > 0 && (
        <div className={styles.listCard}>
          {sessoes.map((sessao) => (
            <div key={sessao.idSessao} className={styles.listItem}>
              <div>
                <strong>{sessao.nomeSessao}</strong>
                <div>{new Date(sessao.dataHoraSessao).toLocaleString("pt-BR")}</div>
                <div>Status: {sessao.statusSessao}</div>
                <div>Capacidade: {sessao.capacidade ?? "Padrão do evento"}</div>
              </div>
              {isAdmin && (
                <div className={styles.actionsInline}>
                  <button type="button" className={styles.btnCancel} onClick={() => onEditSession(sessao)}>
                    Editar
                  </button>
                  <button
                    type="button"
                    className={styles.btnDelete}
                    onClick={() => onRemoveSession(sessao)}
                    disabled={deletingSessionId !== null}
                  >
                    {deletingSessionId === sessao.idSessao ? "Removendo..." : "Remover"}
                  </button>
                </div>
              )}
            </div>
          ))}
        </div>
      )}

      {isAdmin && (
        <form onSubmit={onSubmitSession} className={styles.formCard} noValidate>
          <h3>{form.editando ? "Editar Sessão" : "Nova Sessão"}</h3>

          <label className={styles.label} htmlFor="session-name">
            Nome da sessão <span className={styles.required}>*</span>
          </label>
          <input
            id="session-name"
            type="text"
            className={styles.input}
            placeholder="Nome da sessão"
            value={form.nome}
            onChange={(e) => onSessionNameChange(e.target.value)}
            required
          />
          {form.fieldErrors.nomeSessao && <p className={styles.errorText}>{form.fieldErrors.nomeSessao}</p>}

          <label className={styles.label} htmlFor="session-date-time">
            Data e hora <span className={styles.required}>*</span>
          </label>
          <input
            id="session-date-time"
            type="datetime-local"
            className={styles.input}
            value={form.dataHora}
            onChange={(e) => onSessionDateTimeChange(e.target.value)}
            required
          />
          {form.fieldErrors.dataHoraSessao && (
            <p className={styles.errorText}>{form.fieldErrors.dataHoraSessao}</p>
          )}

          <label className={styles.label} htmlFor="session-status">Status da sessão</label>
          <select
            id="session-status"
            className={styles.select}
            value={form.status}
            onChange={(e) => onSessionStatusChange(e.target.value as SessionStatus)}
          >
            {SESSION_STATUS_OPTIONS.map((status) => (
              <option key={status} value={status}>
                {status === "ATIVO" ? "Ativo" : status === "ESGOTADO" ? "Esgotado" : "Cancelado"}
              </option>
            ))}
          </select>
          {form.fieldErrors.statusSessao && <p className={styles.errorText}>{form.fieldErrors.statusSessao}</p>}

          <label className={styles.label} htmlFor="session-capacity">Capacidade da sessão</label>
          <input
            id="session-capacity"
            type="number"
            className={styles.input}
            placeholder="Capacidade (opcional)"
            min="1"
            step="1"
            value={form.capacidade}
            onChange={(e) => onSessionCapacityChange(e.target.value)}
          />
          {form.fieldErrors.capacidade && <p className={styles.errorText}>{form.fieldErrors.capacidade}</p>}
          {form.formError && <p className={styles.errorText}>{form.formError}</p>}

          <button type="submit" className={styles.btnSave}>
            {form.editando ? "Atualizar Sessão" : "Criar Sessão"}
          </button>
        </form>
      )}
    </div>
  );
}

export function getSessionEditValues(sessao: SessaoEventoResponse) {
  return {
    nome: sessao.nomeSessao,
    dataHora: toDateTimeLocal(sessao.dataHoraSessao),
    status: sessao.statusSessao as SessionStatus,
    capacidade: sessao.capacidade === null ? "" : String(sessao.capacidade),
  };
}
