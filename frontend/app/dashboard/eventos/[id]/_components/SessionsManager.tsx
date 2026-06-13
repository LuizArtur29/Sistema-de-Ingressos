import { FormEvent } from "react";
import { Button, Card, SelectField, TextField } from "@/components/ui";
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
                  <Button type="button" variant="secondary" onClick={() => onEditSession(sessao)}>
                    Editar
                  </Button>
                  <Button
                    type="button"
                    variant="danger"
                    onClick={() => onRemoveSession(sessao)}
                    disabled={deletingSessionId !== null}
                  >
                    {deletingSessionId === sessao.idSessao ? "Removendo..." : "Remover"}
                  </Button>
                </div>
              )}
            </div>
          ))}
        </div>
      )}

      {isAdmin && (
        <Card className={styles.inlineFormCard}>
          <form onSubmit={onSubmitSession} noValidate>
            <h3>{form.editando ? "Editar Sessão" : "Nova Sessão"}</h3>

            <TextField
              id="session-name"
              label="Nome da sessão"
              type="text"
              placeholder="Nome da sessão"
              value={form.nome}
              onChange={(e) => onSessionNameChange(e.target.value)}
              required
              error={form.fieldErrors.nomeSessao}
            />

            <TextField
              id="session-date-time"
              label="Data e hora"
              type="datetime-local"
              value={form.dataHora}
              onChange={(e) => onSessionDateTimeChange(e.target.value)}
              required
              error={form.fieldErrors.dataHoraSessao}
            />

            <SelectField
              id="session-status"
              label="Status da sessão"
              value={form.status}
              onChange={(e) => onSessionStatusChange(e.target.value as SessionStatus)}
              error={form.fieldErrors.statusSessao}
            >
              {SESSION_STATUS_OPTIONS.map((status) => (
                <option key={status} value={status}>
                  {status === "ATIVO" ? "Ativo" : status === "ESGOTADO" ? "Esgotado" : "Cancelado"}
                </option>
              ))}
            </SelectField>

            <TextField
              id="session-capacity"
              label="Capacidade da sessão"
              type="number"
              placeholder="Capacidade (opcional)"
              min="1"
              step="1"
              value={form.capacidade}
              onChange={(e) => onSessionCapacityChange(e.target.value)}
              error={form.fieldErrors.capacidade}
            />
            {form.formError && <p className={styles.errorText}>{form.formError}</p>}

            <Button type="submit">
              {form.editando ? "Atualizar Sessão" : "Criar Sessão"}
            </Button>
          </form>
        </Card>
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
