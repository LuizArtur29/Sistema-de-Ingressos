"use client";

import { FormEvent, useEffect, useState } from "react";
import Link from "next/link";
import { useParams, useRouter } from "next/navigation";
import { isAxiosError } from "axios";
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
  ApiProblemDetail,
  EventoResponse,
  EventoCreateRequest,
  SessaoEventoResponse,
  SessaoEventoRequest,
  TipoIngressoResponse,
  TipoIngressoCreateRequest,
  EventoStatus,
} from "@/services/types";
import { EventoFieldErrors, validateEventoForm } from "@/lib/validation/evento";
import styles from "./page.module.css";

type FormState = Omit<EventoCreateRequest, "capacidadeTotal"> & {
  capacidadeTotal: string;
};

type FieldErrors = EventoFieldErrors<FormState>;
type SessaoStatus = "ATIVO" | "ESGOTADO" | "CANCELADO";
type SessaoFieldErrors = Partial<Record<"nomeSessao" | "dataHoraSessao" | "statusSessao" | "capacidade", string>>;
type TipoFieldErrors = Partial<Record<"nomeSetor" | "preco" | "quantidadeTotal" | "lote", string>>;

const initialFormState: FormState = {
  nome: "",
  descricao: "",
  dataInicio: "",
  dataFim: "",
  local: "",
  capacidadeTotal: "",
  status: "ATIVO" as EventoStatus,
};

const EVENTO_FIELD_NAMES = ["nome", "descricao", "dataInicio", "dataFim", "local", "capacidadeTotal", "status"] as const;
const SESSAO_FIELD_NAMES = ["nomeSessao", "dataHoraSessao", "statusSessao", "capacidade"] as const;
const TIPO_FIELD_NAMES = ["nomeSetor", "preco", "quantidadeTotal", "lote"] as const;
const SESSAO_STATUS_VALIDOS: SessaoStatus[] = ["ATIVO", "ESGOTADO", "CANCELADO"];

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
    capacidadeTotal: String(evento.capacidadeTotal),
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

function mapApiFieldErrors<T extends string>(
  data: ApiProblemDetail | undefined,
  fieldNames: readonly T[]
): { fieldErrors: Partial<Record<T, string>>; fallbackMessage: string | null } {
  const fieldErrors: Partial<Record<T, string>> = {};
  const unmappedErrors: string[] = [];

  data?.errors?.forEach((item) => {
    if (fieldNames.includes(item.field as T)) {
      fieldErrors[item.field as T] = item.message;
      return;
    }
    unmappedErrors.push(item.message);
  });

  return {
    fieldErrors,
    fallbackMessage: unmappedErrors[0] ?? data?.detail ?? null,
  };
}

function isPositiveIntegerText(value: string) {
  const normalized = value.trim();
  const parsed = Number(normalized);

  return normalized !== "" && Number.isInteger(parsed) && parsed > 0;
}

function isNonNegativeNumberText(value: string) {
  const normalized = value.trim();
  const parsed = Number(normalized);

  return normalized !== "" && Number.isFinite(parsed) && parsed >= 0;
}

function validateSessaoForm(
  nomeSessao: string,
  dataHoraSessao: string,
  statusSessao: string,
  capacidade: string
): SessaoFieldErrors {
  const errors: SessaoFieldErrors = {};

  if (!nomeSessao.trim()) errors.nomeSessao = "Informe o nome da sessão.";
  if (!dataHoraSessao) errors.dataHoraSessao = "Informe a data e hora da sessão.";
  if (!SESSAO_STATUS_VALIDOS.includes(statusSessao as SessaoStatus)) {
    errors.statusSessao = "Selecione um status válido para a sessão.";
  }
  if (capacidade.trim() && !isPositiveIntegerText(capacidade)) {
    errors.capacidade = "A capacidade da sessão deve ser maior que zero.";
  }

  return errors;
}

function validateTipoIngressoForm(
  nomeSetor: string,
  preco: string,
  quantidadeTotal: string,
  lote: string
): TipoFieldErrors {
  const errors: TipoFieldErrors = {};

  if (!nomeSetor.trim()) errors.nomeSetor = "Informe o setor.";
  if (!isNonNegativeNumberText(preco)) errors.preco = "O preço deve ser maior ou igual a zero.";
  if (!isPositiveIntegerText(quantidadeTotal)) {
    errors.quantidadeTotal = "A quantidade deve ser maior que zero.";
  }
  if (!isPositiveIntegerText(lote)) errors.lote = "O lote deve ser maior que zero.";

  return errors;
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
  const [fieldErrors, setFieldErrors] = useState<FieldErrors>({});

  const [sessoes, setSessoes] = useState<SessaoEventoResponse[]>([]);
  const [loadingSessoes, setLoadingSessoes] = useState(false);
  const [errorSessoes, setErrorSessoes] = useState<string | null>(null);
  const [sessaoSelecionada, setSessaoSelecionada] = useState<number | null>(null);

  const [tiposIngresso, setTiposIngresso] = useState<TipoIngressoResponse[]>([]);
  const [loadingTipos, setLoadingTipos] = useState(false);
  const [errorTipos, setErrorTipos] = useState<string | null>(null);

  const [sessaoNome, setSessaoNome] = useState("");
  const [sessaoDataHora, setSessaoDataHora] = useState("");
  const [sessaoStatus, setSessaoStatus] = useState<SessaoStatus>("ATIVO");
  const [sessaoCapacidade, setSessaoCapacidade] = useState("");
  const [sessaoEditando, setSessaoEditando] = useState<SessaoEventoResponse | null>(null);
  const [sessaoFieldErrors, setSessaoFieldErrors] = useState<SessaoFieldErrors>({});
  const [sessaoFormError, setSessaoFormError] = useState<string | null>(null);

  const [tipoNomeSetor, setTipoNomeSetor] = useState("");
  const [tipoPreco, setTipoPreco] = useState("");
  const [tipoQuantidadeTotal, setTipoQuantidadeTotal] = useState("");
  const [tipoLote, setTipoLote] = useState("");
  const [tipoFieldErrors, setTipoFieldErrors] = useState<TipoFieldErrors>({});
  const [tipoFormError, setTipoFormError] = useState<string | null>(null);

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
    setFieldErrors((current) => ({ ...current, [field]: undefined }));
    setError(null);
  };

  const validateEvento = () => {
    const errors = validateEventoForm(form);
    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSave = async (e: FormEvent) => {
    e.preventDefault();
    if (saving) return;

    setError(null);

    if (!validateEvento()) {
      showToast("Corrija os campos destacados.", "error");
      return;
    }

    setSaving(true);

    try {
      const payload: EventoCreateRequest = {
        ...form,
        nome: form.nome.trim(),
        descricao: form.descricao.trim(),
        local: form.local.trim(),
        capacidadeTotal: Number(form.capacidadeTotal),
      };

      await atualizarEvento(eventId, payload);
      showToast("Evento atualizado com sucesso.", "success");
      router.push("/dashboard");
    } catch (err: unknown) {
      const data = isAxiosError<ApiProblemDetail>(err) ? err.response?.data : undefined;
      const { fieldErrors: apiErrors, fallbackMessage } = mapApiFieldErrors(data, EVENTO_FIELD_NAMES);

      setFieldErrors(apiErrors);
      setError(fallbackMessage || "Não foi possível atualizar o evento.");
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
    setSessaoFormError(null);

    const errors = validateSessaoForm(sessaoNome, sessaoDataHora, sessaoStatus, sessaoCapacidade);
    setSessaoFieldErrors(errors);

    if (Object.keys(errors).length > 0) {
      showToast("Corrija os campos destacados.", "error");
      return;
    }

    const payload: SessaoEventoRequest = {
      nomeSessao: sessaoNome.trim(),
      dataHoraSessao: toLocalDateTime(sessaoDataHora),
      statusSessao: sessaoStatus,
      capacidade: sessaoCapacidade.trim() === "" ? null : Number(sessaoCapacidade),
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
      setSessaoFieldErrors({});
      setSessaoFormError(null);
    } catch (err: unknown) {
      const data = isAxiosError<ApiProblemDetail>(err) ? err.response?.data : undefined;
      const { fieldErrors: apiErrors, fallbackMessage } = mapApiFieldErrors(data, SESSAO_FIELD_NAMES);

      setSessaoFieldErrors(apiErrors);
      setSessaoFormError(fallbackMessage || "Não foi possível salvar a sessão.");
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
    setTipoFormError(null);

    const errors = validateTipoIngressoForm(tipoNomeSetor, tipoPreco, tipoQuantidadeTotal, tipoLote);
    setTipoFieldErrors(errors);

    if (Object.keys(errors).length > 0) {
      showToast("Corrija os campos destacados.", "error");
      return;
    }

    const payload: TipoIngressoCreateRequest = {
      nomeSetor: tipoNomeSetor.trim(),
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
      setTipoFieldErrors({});
      setTipoFormError(null);
    } catch (err: unknown) {
      const data = isAxiosError<ApiProblemDetail>(err) ? err.response?.data : undefined;
      const { fieldErrors: apiErrors, fallbackMessage } = mapApiFieldErrors(data, TIPO_FIELD_NAMES);

      setTipoFieldErrors(apiErrors);
      setTipoFormError(fallbackMessage || "Não foi possível criar o tipo de ingresso.");
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
          <form onSubmit={handleSave} noValidate>
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
                {fieldErrors.nome && <p className={styles.errorText}>{fieldErrors.nome}</p>}
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
                {fieldErrors.descricao && <p className={styles.errorText}>{fieldErrors.descricao}</p>}
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
                  {fieldErrors.dataInicio && <p className={styles.errorText}>{fieldErrors.dataInicio}</p>}
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
                  {fieldErrors.dataFim && <p className={styles.errorText}>{fieldErrors.dataFim}</p>}
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
                {fieldErrors.local && <p className={styles.errorText}>{fieldErrors.local}</p>}
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
                      step="1"
                      className={styles.input}
                      placeholder="Ex.: 500"
                      value={form.capacidadeTotal}
                      onChange={(e) => updateField("capacidadeTotal", e.target.value)}
                      required
                  />
                  {fieldErrors.capacidadeTotal && (
                    <p className={styles.errorText}>{fieldErrors.capacidadeTotal}</p>
                  )}
                </div>
                <div className={styles.col}>
                  <label className={styles.label}>
                    Status <span className={styles.required}>*</span>
                  </label>
                  <select
                      className={styles.select}
                      value={form.status}
                      onChange={(e) => updateField("status", e.target.value as EventoStatus)}
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
                              setSessaoStatus(sessao.statusSessao as SessaoStatus);
                              setSessaoCapacidade(sessao.capacidade === null ? "" : String(sessao.capacidade));
                              setSessaoFieldErrors({});
                              setSessaoFormError(null);
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

          <form onSubmit={handleSalvarSessao} className={styles.formCard} noValidate>
            <h3>{sessaoEditando ? "Editar Sessão" : "Nova Sessão"}</h3>

            <input
                type="text"
                className={styles.input}
                placeholder="Nome da sessão"
                value={sessaoNome}
                onChange={(e) => {
                  setSessaoNome(e.target.value);
                  setSessaoFieldErrors((current) => ({ ...current, nomeSessao: undefined }));
                  setSessaoFormError(null);
                }}
                required
            />
            {sessaoFieldErrors.nomeSessao && (
                <p className={styles.errorText}>{sessaoFieldErrors.nomeSessao}</p>
            )}

            <input
                type="datetime-local"
                className={styles.input}
                value={sessaoDataHora}
                onChange={(e) => {
                  setSessaoDataHora(e.target.value);
                  setSessaoFieldErrors((current) => ({ ...current, dataHoraSessao: undefined }));
                  setSessaoFormError(null);
                }}
                required
            />
            {sessaoFieldErrors.dataHoraSessao && (
                <p className={styles.errorText}>{sessaoFieldErrors.dataHoraSessao}</p>
            )}

            <select
                className={styles.select}
                value={sessaoStatus}
                onChange={(e) => {
                  setSessaoStatus(e.target.value as SessaoStatus);
                  setSessaoFieldErrors((current) => ({ ...current, statusSessao: undefined }));
                  setSessaoFormError(null);
                }}
            >
              <option value="ATIVO">Ativo</option>
              <option value="ESGOTADO">Esgotado</option>
              <option value="CANCELADO">Cancelado</option>
            </select>
            {sessaoFieldErrors.statusSessao && (
                <p className={styles.errorText}>{sessaoFieldErrors.statusSessao}</p>
            )}

            <input
                type="number"
                className={styles.input}
                placeholder="Capacidade (opcional)"
                min="1"
                step="1"
                value={sessaoCapacidade}
                onChange={(e) => {
                  setSessaoCapacidade(e.target.value);
                  setSessaoFieldErrors((current) => ({ ...current, capacidade: undefined }));
                  setSessaoFormError(null);
                }}
            />
            {sessaoFieldErrors.capacidade && (
                <p className={styles.errorText}>{sessaoFieldErrors.capacidade}</p>
            )}
            {sessaoFormError && <p className={styles.errorText}>{sessaoFormError}</p>}

            <button type="submit" className={styles.btnSave}>
              {sessaoEditando ? "Atualizar Sessão" : "Criar Sessão"}
            </button>
          </form>
        </div>

        <div className={styles.section}>
          <h2 className={styles.sectionTitle}>Tipos de Ingresso</h2>

          {sessoes.length > 0 && (
              <select
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
              <form onSubmit={handleCriarTipo} className={styles.formCard} noValidate>
                <h3>Novo Tipo de Ingresso</h3>

                <input
                    type="text"
                    className={styles.input}
                    placeholder="Setor"
                    value={tipoNomeSetor}
                    onChange={(e) => {
                      setTipoNomeSetor(e.target.value);
                      setTipoFieldErrors((current) => ({ ...current, nomeSetor: undefined }));
                      setTipoFormError(null);
                    }}
                    required
                />
                {tipoFieldErrors.nomeSetor && (
                    <p className={styles.errorText}>{tipoFieldErrors.nomeSetor}</p>
                )}

                <input
                    type="number"
                    className={styles.input}
                    placeholder="Preço"
                    min="0"
                    step="0.01"
                    value={tipoPreco}
                    onChange={(e) => {
                      setTipoPreco(e.target.value);
                      setTipoFieldErrors((current) => ({ ...current, preco: undefined }));
                      setTipoFormError(null);
                    }}
                    required
                />
                {tipoFieldErrors.preco && (
                    <p className={styles.errorText}>{tipoFieldErrors.preco}</p>
                )}

                <input
                    type="number"
                    className={styles.input}
                    placeholder="Quantidade total"
                    min="1"
                    step="1"
                    value={tipoQuantidadeTotal}
                    onChange={(e) => {
                      setTipoQuantidadeTotal(e.target.value);
                      setTipoFieldErrors((current) => ({ ...current, quantidadeTotal: undefined }));
                      setTipoFormError(null);
                    }}
                    required
                />
                {tipoFieldErrors.quantidadeTotal && (
                    <p className={styles.errorText}>{tipoFieldErrors.quantidadeTotal}</p>
                )}

                <input
                    type="number"
                    className={styles.input}
                    placeholder="Lote"
                    min="1"
                    step="1"
                    value={tipoLote}
                    onChange={(e) => {
                      setTipoLote(e.target.value);
                      setTipoFieldErrors((current) => ({ ...current, lote: undefined }));
                      setTipoFormError(null);
                    }}
                    required
                />
                {tipoFieldErrors.lote && (
                    <p className={styles.errorText}>{tipoFieldErrors.lote}</p>
                )}
                {tipoFormError && <p className={styles.errorText}>{tipoFormError}</p>}

                <button type="submit" className={styles.btnSave}>
                  Criar Tipo
                </button>
              </form>
          )}
        </div>
      </div>
  );
}
