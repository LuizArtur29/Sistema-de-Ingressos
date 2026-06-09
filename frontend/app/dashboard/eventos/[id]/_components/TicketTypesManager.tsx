import { FormEvent } from "react";
import { SessaoEventoResponse, TipoIngressoResponse } from "@/services/types";
import { TicketTypeFieldErrors } from "../_utils/eventDetailsTypes";
import styles from "../page.module.css";

type TicketTypesManagerProps = {
  sessoes: SessaoEventoResponse[];
  sessaoSelecionada: number | null;
  tiposIngresso: TipoIngressoResponse[];
  loadingTipos: boolean;
  errorTipos: string | null;
  isAdmin: boolean;
  form: {
    nomeSetor: string;
    preco: string;
    quantidadeTotal: string;
    lote: string;
    fieldErrors: TicketTypeFieldErrors;
    formError: string | null;
  };
  onSelectedSessionChange: (idSessao: number) => void;
  onSubmitTicketType: (event: FormEvent) => void;
  onSectorChange: (value: string) => void;
  onPriceChange: (value: string) => void;
  onQuantityChange: (value: string) => void;
  onLotChange: (value: string) => void;
};

export default function TicketTypesManager({
  sessoes,
  sessaoSelecionada,
  tiposIngresso,
  loadingTipos,
  errorTipos,
  isAdmin,
  form,
  onSelectedSessionChange,
  onSubmitTicketType,
  onSectorChange,
  onPriceChange,
  onQuantityChange,
  onLotChange,
}: TicketTypesManagerProps) {
  return (
    <div className={styles.section}>
      <h2 className={styles.sectionTitle}>Tipos de Ingresso</h2>

      {sessoes.length > 0 && (
        <>
          <label className={styles.label} htmlFor="ticket-session-select">Sessão</label>
          <select
            id="ticket-session-select"
            className={styles.select}
            value={sessaoSelecionada ?? ""}
            onChange={(e) => onSelectedSessionChange(Number(e.target.value))}
          >
            {sessoes.map((sessao) => (
              <option key={sessao.idSessao} value={sessao.idSessao}>
                {sessao.nomeSessao}
              </option>
            ))}
          </select>
        </>
      )}

      {loadingTipos && <p className={styles.feedback}>Carregando tipos de ingresso...</p>}
      {errorTipos && <p className={styles.errorText}>{errorTipos}</p>}

      {!loadingTipos && !errorTipos && tiposIngresso.length === 0 && (
        <p className={styles.feedback}>Nenhum tipo de ingresso cadastrado.</p>
      )}

      {!loadingTipos && tiposIngresso.length > 0 && (
        <div className={styles.listCard}>
          {tiposIngresso.map((tipo) => (
            <div key={tipo.idTipoIngresso} className={styles.listItem}>
              <div>
                <strong>{tipo.nomeSetor}</strong>
                <div>Preço: R$ {tipo.preco.toFixed(2)}</div>
                <div>Quantidade total: {tipo.quantidadeTotal}</div>
                <div>Disponível: {tipo.quantidadeDisponivel}</div>
                <div>Lote: {tipo.lote}</div>
              </div>
            </div>
          ))}
        </div>
      )}

      {isAdmin && sessaoSelecionada && (
        <form onSubmit={onSubmitTicketType} className={styles.formCard} noValidate>
          <h3>Novo Tipo de Ingresso</h3>

          <label className={styles.label} htmlFor="ticket-sector">
            Setor <span className={styles.required}>*</span>
          </label>
          <input
            id="ticket-sector"
            type="text"
            className={styles.input}
            placeholder="Setor"
            value={form.nomeSetor}
            onChange={(e) => onSectorChange(e.target.value)}
            required
          />
          {form.fieldErrors.nomeSetor && <p className={styles.errorText}>{form.fieldErrors.nomeSetor}</p>}

          <label className={styles.label} htmlFor="ticket-price">
            Preço <span className={styles.required}>*</span>
          </label>
          <input
            id="ticket-price"
            type="number"
            className={styles.input}
            placeholder="Preço"
            min="0"
            step="0.01"
            value={form.preco}
            onChange={(e) => onPriceChange(e.target.value)}
            required
          />
          {form.fieldErrors.preco && <p className={styles.errorText}>{form.fieldErrors.preco}</p>}

          <label className={styles.label} htmlFor="ticket-quantity">
            Quantidade total <span className={styles.required}>*</span>
          </label>
          <input
            id="ticket-quantity"
            type="number"
            className={styles.input}
            placeholder="Quantidade total"
            min="1"
            step="1"
            value={form.quantidadeTotal}
            onChange={(e) => onQuantityChange(e.target.value)}
            required
          />
          {form.fieldErrors.quantidadeTotal && (
            <p className={styles.errorText}>{form.fieldErrors.quantidadeTotal}</p>
          )}

          <label className={styles.label} htmlFor="ticket-lot">
            Lote <span className={styles.required}>*</span>
          </label>
          <input
            id="ticket-lot"
            type="number"
            className={styles.input}
            placeholder="Lote"
            min="1"
            step="1"
            value={form.lote}
            onChange={(e) => onLotChange(e.target.value)}
            required
          />
          {form.fieldErrors.lote && <p className={styles.errorText}>{form.fieldErrors.lote}</p>}
          {form.formError && <p className={styles.errorText}>{form.formError}</p>}

          <button type="submit" className={styles.btnSave}>
            Criar Tipo
          </button>
        </form>
      )}
    </div>
  );
}
