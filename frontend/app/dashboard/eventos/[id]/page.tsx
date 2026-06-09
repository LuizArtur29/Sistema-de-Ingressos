"use client";

import { FormEvent, useEffect, useState } from "react";
import Link from "next/link";
import { useParams, useRouter } from "next/navigation";
import { useToast } from "@/components/ToastProvider";
import { atualizarEvento, buscarEventoPorId, excluirEvento } from "@/services/eventos";
import {
  listarSessoesPorEvento,
  criarSessao,
  atualizarSessao,
  excluirSessao,
} from "@/services/sessoes";
import { listarTiposPorSessao, criarTipoIngresso } from "@/services/tiposIngressos";
import {
  EventoResponse,
  EventoCreateRequest,
  SessaoEventoResponse,
  SessaoEventoRequest,
  TipoIngressoResponse,
  TipoIngressoCreateRequest,
  EventoStatus,
} from "@/services/types";
import styles from "./page.module.css";

type FormState = EventoCreateRequest;

const initialFormState: FormState = {
  nome: "",
  descricao: "",
  dataInicio: "",
  dataFim: "",
  local: "",
  capacidadeTotal: 0,
  status: "ATIVO" as EventoStatus,
};

const statusLabelMap: Record<EventoStatus, string> = {
  ATIVO: "Ativo",
  CANCELADO: "Cancelado",
  FINALIZADO: "Finalizado",
};

function normalizeDate(value?: string) {
  if (!value) return "";
  return value.slice(0, 10);
}

function toFormState(evento: EventoResponse): FormState {
  return {
    nome: evento.nome,
    descricao: evento.descricao,
    dataInicio: normalizeDate(evento.dataInicio),
    dataFim: normalizeDate(evento.dataFim),
    local: evento.local,
    capacidadeTotal: evento.capacidadeTotal,
    status: evento.status as EventoStatus,
  };
}

function toLocalDateTime(value: string) {
  if (!value) return "";
  if (value.length === 19) return value;
  return `${value}:00`;
}

function toDateTimeLocal(value: string) {
  if (!value) return "";
  return value.slice(0, 16);
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

  const [sessoes, setSessoes] = useState<SessaoEventoResponse[]>([]);
  const [loadingSessoes, setLoadingSessoes] = useState(false);
  const [errorSessoes, setErrorSessoes] = useState<string | null>(null);
  const [sessaoSelecionada, setSessaoSelecionada] = useState<number | null>(null);

  const [tiposIngresso, setTiposIngresso] = useState<TipoIngressoResponse[]>([]);
  const [loadingTipos, setLoadingTipos] = useState(false);
  const [errorTipos, setErrorTipos] = useState<string | null>(null);

  const [sessaoNome, setSessaoNome] = useState("");
  const [sessaoDataHora, setSessaoDataHora] = useState("");
  const [sessaoStatus, setSessaoStatus] = useState<EventoStatus>("ATIVO");
  const [sessaoCapacidade, setSessaoCapacidade] = useState<number | "">("");
  const [sessaoEditando, setSessaoEditando] = useState<SessaoEventoResponse | null>(null);

  const [tipoNomeSetor, setTipoNomeSetor] = useState("");
  const [tipoPreco, setTipoPreco] = useState<number | "">("");
  const [tipoQuantidadeTotal, setTipoQuantidadeTotal] = useState<number | "">("");
  const [tipoLote, setTipoLote] = useState<number | "">("");

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

    const loadSessoes = async () => {
      setLoadingSessoes(true);
      setErrorSessoes(null);
      try {
        const data = await listarSessoesPorEvento(eventId);
        setSessoes(data);
        if (data.length > 0) {
          setSessaoSelecionada(data[0].idSessao);
        }
      } catch {
        setErrorSessoes("Não foi possível carregar as sessões.");
        showToast("Erro ao carregar sessões.", "error");
      } finally {
        setLoadingSessoes(false);
      }
    };

    load();
    loadSessoes();
  }, [eventId, showToast]);

  useEffect(() => {
    if (!sessaoSelecionada) {
      setTiposIngresso([]);
      return;
    }

    const loadTipos = async () => {
      setLoadingTipos(true);
      setErrorTipos(null);
      try {
        const data = await listarTiposPorSessao(sessaoSelecionada);
        setTiposIngresso(data);
      } catch {
        setErrorTipos("Não foi possível carregar os tipos de ingresso.");
        showToast("Erro ao carregar tipos de ingresso.", "error");
      } finally {
        setLoadingTipos(false);
      }
    };

    loadTipos();
  }, [sessaoSelecionada, showToast]);

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

  const handleSalvarSessao = async (e: FormEvent) => {
    e.preventDefault();

    const payload: SessaoEventoRequest = {
      nomeSessao: sessaoNome,
      dataHoraSessao: toLocalDateTime(sessaoDataHora),
      statusSessao: sessaoStatus,
      capacidade: sessaoCapacidade === "" ? null : Number(sessaoCapacidade),
      eventoPai: { id: eventId },
    };

    try {
      if (sessaoEditando) {
        await atualizarSessao(sessaoEditando.idSessao, payload);
        showToast("Sessão atualizada com sucesso.", "success");
      } else {
        await criarSessao(payload);
        showToast("Sessão criada com sucesso.", "success");
      }

      const data = await listarSessoesPorEvento(eventId);
      setSessoes(data);
      if (!sessaoSelecionada && data.length > 0) {
        setSessaoSelecionada(data[0].idSessao);
      }

      setSessaoNome("");
      setSessaoDataHora("");
      setSessaoStatus("ATIVO");
      setSessaoCapacidade("");
      setSessaoEditando(null);
    } catch {
      showToast("Erro ao salvar sessão.", "error");
    }
  };

  const handleRemoverSessao = async (idSessao: number) => {
    const confirmed = window.confirm("Deseja remover esta sessão?");
    if (!confirmed) return;

    try {
      await excluirSessao(idSessao);
      showToast("Sessão removida.", "success");

      const data = await listarSessoesPorEvento(eventId);
      setSessoes(data);
      setSessaoSelecionada(data.length ? data[0].idSessao : null);
    } catch {
      showToast("Erro ao remover sessão.", "error");
    }
  };

  const handleCriarTipo = async (e: FormEvent) => {
    e.preventDefault();
    if (!sessaoSelecionada) return;

    const payload: TipoIngressoCreateRequest = {
      nomeSetor: tipoNomeSetor,
      preco: Number(tipoPreco),
      quantidadeTotal: Number(tipoQuantidadeTotal),
      lote: Number(tipoLote),
      sessaoId: sessaoSelecionada,
    };

    try {
      await criarTipoIngresso(payload);
      showToast("Tipo de ingresso criado.", "success");

      const data = await listarTiposPorSessao(sessaoSelecionada);
      setTiposIngresso(data);

      setTipoNomeSetor("");
      setTipoPreco("");
      setTipoQuantidadeTotal("");
      setTipoLote("");
    } catch {
      showToast("Erro ao criar tipo de ingresso.", "error");
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
          <form onSubmit={handleSave} aria-describedby={error ? "event-form-error" : undefined}>
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
                    onChange={(e) => updateField("nome", e.target.value)}
                    required
                />
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
                    onChange={(e) => updateField("descricao", e.target.value)}
                    required
                />
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
                      onChange={(e) => updateField("dataInicio", e.target.value)}
                      required
                  />
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
                      onChange={(e) => updateField("dataFim", e.target.value)}
                      required
                  />
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
                    onChange={(e) => updateField("local", e.target.value)}
                    required
                />
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
                      className={styles.input}
                      placeholder="Ex.: 500"
                      value={form.capacidadeTotal || ""}
                      onChange={(e) => updateField("capacidadeTotal", Number(e.target.value))}
                      required
                  />
                </div>
                <div className={styles.col}>
                  <label className={styles.label} htmlFor="event-status">
                    Status <span className={styles.required}>*</span>
                  </label>
                  <select
                      id="event-status"
                      className={styles.select}
                      value={form.status}
                      onChange={(e) => updateField("status", e.target.value as EventoStatus)}
                      required
                  >
                    <option value="ATIVO">Ativo</option>
                    <option value="CANCELADO">Cancelado</option>
                    <option value="FINALIZADO">Finalizado</option>
                  </select>
                </div>
              </div>
            </div>

            {error && <p id="event-form-error" className={styles.errorText}>{error}</p>}

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
                      <div className={styles.actionsInline}>
                        <button
                            type="button"
                            className={styles.btnCancel}
                            onClick={() => {
                              setSessaoEditando(sessao);
                              setSessaoNome(sessao.nomeSessao);
                              setSessaoDataHora(toDateTimeLocal(sessao.dataHoraSessao));
                              setSessaoStatus(sessao.statusSessao as EventoStatus);
                              setSessaoCapacidade(sessao.capacidade ?? "");
                            }}
                        >
                          Editar
                        </button>
                        <button
                            type="button"
                            className={styles.btnDelete}
                            onClick={() => handleRemoverSessao(sessao.idSessao)}
                        >
                          Remover
                        </button>
                      </div>
                    </div>
                ))}
              </div>
          )}

          <form onSubmit={handleSalvarSessao} className={styles.formCard}>
            <h3>{sessaoEditando ? "Editar Sessão" : "Nova Sessão"}</h3>

            <label className={styles.label} htmlFor="session-name">
              Nome da sessão <span className={styles.required}>*</span>
            </label>
            <input
                id="session-name"
                type="text"
                className={styles.input}
                placeholder="Nome da sessão"
                value={sessaoNome}
                onChange={(e) => setSessaoNome(e.target.value)}
                required
            />

            <label className={styles.label} htmlFor="session-date-time">
              Data e hora <span className={styles.required}>*</span>
            </label>
            <input
                id="session-date-time"
                type="datetime-local"
                className={styles.input}
                value={sessaoDataHora}
                onChange={(e) => setSessaoDataHora(e.target.value)}
                required
            />

            <label className={styles.label} htmlFor="session-status">Status da sessão</label>
            <select
                id="session-status"
                className={styles.select}
                value={sessaoStatus}
                onChange={(e) => setSessaoStatus(e.target.value as EventoStatus)}
            >
              <option value="ATIVO">Ativo</option>
              <option value="ESGOTADO">Esgotado</option>
              <option value="CANCELADO">Cancelado</option>
            </select>

            <label className={styles.label} htmlFor="session-capacity">Capacidade da sessão</label>
            <input
                id="session-capacity"
                type="number"
                className={styles.input}
                placeholder="Capacidade (opcional)"
                value={sessaoCapacidade}
                onChange={(e) => setSessaoCapacidade(e.target.value === "" ? "" : Number(e.target.value))}
            />

            <button type="submit" className={styles.btnSave}>
              {sessaoEditando ? "Atualizar Sessão" : "Criar Sessão"}
            </button>
          </form>
        </div>

        <div className={styles.section}>
          <h2 className={styles.sectionTitle}>Tipos de Ingresso</h2>

          {sessoes.length > 0 && (
              <>
              <label className={styles.label} htmlFor="ticket-session-select">Sessão</label>
              <select
                  id="ticket-session-select"
                  className={styles.select}
                  value={sessaoSelecionada ?? ""}
                  onChange={(e) => setSessaoSelecionada(Number(e.target.value))}
              >
                {sessoes.map((s) => (
                    <option key={s.idSessao} value={s.idSessao}>
                      {s.nomeSessao}
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

          {sessaoSelecionada && (
              <form onSubmit={handleCriarTipo} className={styles.formCard}>
                <h3>Novo Tipo de Ingresso</h3>

                <label className={styles.label} htmlFor="ticket-sector">
                  Setor <span className={styles.required}>*</span>
                </label>
                <input
                    id="ticket-sector"
                    type="text"
                    className={styles.input}
                    placeholder="Setor"
                    value={tipoNomeSetor}
                    onChange={(e) => setTipoNomeSetor(e.target.value)}
                    required
                />

                <label className={styles.label} htmlFor="ticket-price">
                  Preço <span className={styles.required}>*</span>
                </label>
                <input
                    id="ticket-price"
                    type="number"
                    className={styles.input}
                    placeholder="Preço"
                    value={tipoPreco}
                    onChange={(e) => setTipoPreco(e.target.value === "" ? "" : Number(e.target.value))}
                    required
                />

                <label className={styles.label} htmlFor="ticket-quantity">
                  Quantidade total <span className={styles.required}>*</span>
                </label>
                <input
                    id="ticket-quantity"
                    type="number"
                    className={styles.input}
                    placeholder="Quantidade total"
                    value={tipoQuantidadeTotal}
                    onChange={(e) =>
                        setTipoQuantidadeTotal(e.target.value === "" ? "" : Number(e.target.value))
                    }
                    required
                />

                <label className={styles.label} htmlFor="ticket-lot">
                  Lote <span className={styles.required}>*</span>
                </label>
                <input
                    id="ticket-lot"
                    type="number"
                    className={styles.input}
                    placeholder="Lote"
                    value={tipoLote}
                    onChange={(e) => setTipoLote(e.target.value === "" ? "" : Number(e.target.value))}
                    required
                />

                <button type="submit" className={styles.btnSave}>
                  Criar Tipo
                </button>
              </form>
          )}
        </div>
      </div>
  );
}
