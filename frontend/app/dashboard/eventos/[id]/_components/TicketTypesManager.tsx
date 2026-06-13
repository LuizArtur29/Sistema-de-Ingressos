import { FormEvent } from "react";
import { Button, Card, SelectField, TextField } from "@/components/ui";
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
        <SelectField
            id="ticket-session-select"
            label="Sessão"
            value={sessaoSelecionada ?? ""}
            onChange={(e) => onSelectedSessionChange(Number(e.target.value))}
          >
            {sessoes.map((sessao) => (
              <option key={sessao.idSessao} value={sessao.idSessao}>
                {sessao.nomeSessao}
              </option>
            ))}
        </SelectField>
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
        <Card className={styles.inlineFormCard}>
          <form onSubmit={onSubmitTicketType} noValidate>
            <h3>Novo Tipo de Ingresso</h3>

            <TextField
              id="ticket-sector"
              label="Setor"
              type="text"
              placeholder="Setor"
              value={form.nomeSetor}
              onChange={(e) => onSectorChange(e.target.value)}
              required
              error={form.fieldErrors.nomeSetor}
            />

            <TextField
              id="ticket-price"
              label="Preço"
              type="number"
              placeholder="Preço"
              min="0"
              step="0.01"
              value={form.preco}
              onChange={(e) => onPriceChange(e.target.value)}
              required
              error={form.fieldErrors.preco}
            />

            <TextField
              id="ticket-quantity"
              label="Quantidade total"
              type="number"
              placeholder="Quantidade total"
              min="1"
              step="1"
              value={form.quantidadeTotal}
              onChange={(e) => onQuantityChange(e.target.value)}
              required
              error={form.fieldErrors.quantidadeTotal}
            />

            <TextField
              id="ticket-lot"
              label="Lote"
              type="number"
              placeholder="Lote"
              min="1"
              step="1"
              value={form.lote}
              onChange={(e) => onLotChange(e.target.value)}
              required
              error={form.fieldErrors.lote}
            />
            {form.formError && <p className={styles.errorText}>{form.formError}</p>}

            <Button type="submit">
              Criar Tipo
            </Button>
          </form>
        </Card>
      )}
    </div>
  );
}
