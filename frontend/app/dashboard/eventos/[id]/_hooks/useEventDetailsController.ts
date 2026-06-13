import { FormEvent, useCallback, useState } from "react";
import { useRouter } from "next/navigation";
import { isAxiosError } from "axios";
import { useToast } from "@/components/ToastProvider";
import { getForbiddenMessage, isForbiddenError } from "@/lib/apiErrors";
import { atualizarEvento, excluirEvento } from "@/services/eventos";
import { criarSessao, atualizarSessao, excluirSessao } from "@/services/sessoes";
import { criarTipoIngresso } from "@/services/tiposIngressos";
import {
  ApiProblemDetail,
  EventoCreateRequest,
  SessaoEventoResponse,
  SessaoEventoRequest,
  TipoIngressoCreateRequest,
} from "@/services/types";
import { validateEventoForm } from "@/lib/validation/evento";
import { getSessionEditValues } from "../_components/SessionsManager";
import { mapApiFieldErrors } from "../_utils/apiFieldErrors";
import { toLocalDateTime } from "../_utils/dateHelpers";
import {
  EVENT_FIELD_NAMES,
  EventFieldErrors,
  EventFormState,
  SESSION_FIELD_NAMES,
  SessionFieldErrors,
  SessionStatus,
  TICKET_TYPE_FIELD_NAMES,
  TicketTypeFieldErrors,
} from "../_utils/eventDetailsTypes";
import { validateSessionForm, validateTicketTypeForm } from "../_utils/formValidation";
import { useEventDetails } from "./useEventDetails";
import { useEventSessions } from "./useEventSessions";
import { useTicketTypes } from "./useTicketTypes";

type DeleteTarget =
  | {
      type: "event";
      name: string;
    }
  | {
      type: "session";
      id: number;
      name: string;
    };

export function useEventDetailsController(eventId: number, isAdmin: boolean) {
  const router = useRouter();
  const { showToast } = useToast();
  const notifyEventLoadError = useCallback(() => showToast("Erro ao carregar evento.", "error"), [showToast]);
  const notifySessionsLoadError = useCallback(() => showToast("Erro ao carregar sessões.", "error"), [showToast]);
  const notifyTicketTypesLoadError = useCallback(
    () => showToast("Erro ao carregar tipos de ingresso.", "error"),
    [showToast]
  );

  const eventDetails = useEventDetails(eventId, notifyEventLoadError);
  const sessions = useEventSessions(eventId, notifySessionsLoadError);
  const ticketTypes = useTicketTypes(sessions.sessaoSelecionada, notifyTicketTypesLoadError);

  const { form, setForm, setError } = eventDetails;
  const [saving, setSaving] = useState(false);
  const [deleting, setDeleting] = useState(false);
  const [deletingSessionId, setDeletingSessionId] = useState<number | null>(null);
  const [deleteTarget, setDeleteTarget] = useState<DeleteTarget | null>(null);
  const [fieldErrors, setFieldErrors] = useState<EventFieldErrors>({});

  const [sessaoNome, setSessaoNome] = useState("");
  const [sessaoDataHora, setSessaoDataHora] = useState("");
  const [sessaoStatus, setSessaoStatus] = useState<SessionStatus>("ATIVO");
  const [sessaoCapacidade, setSessaoCapacidade] = useState("");
  const [sessaoEditando, setSessaoEditando] = useState<SessaoEventoResponse | null>(null);
  const [sessaoFieldErrors, setSessaoFieldErrors] = useState<SessionFieldErrors>({});
  const [sessaoFormError, setSessaoFormError] = useState<string | null>(null);

  const [tipoNomeSetor, setTipoNomeSetor] = useState("");
  const [tipoPreco, setTipoPreco] = useState("");
  const [tipoQuantidadeTotal, setTipoQuantidadeTotal] = useState("");
  const [tipoLote, setTipoLote] = useState("");
  const [tipoFieldErrors, setTipoFieldErrors] = useState<TicketTypeFieldErrors>({});
  const [tipoFormError, setTipoFormError] = useState<string | null>(null);

  const updateField = <K extends keyof EventFormState>(field: K, value: EventFormState[K]) => {
    setForm((current) => ({ ...current, [field]: value }));
    setFieldErrors((current) => ({ ...current, [field]: undefined }));
    setError(null);
  };

  const validateEvento = () => {
    const errors = validateEventoForm(form);
    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSave = async (event: FormEvent) => {
    event.preventDefault();
    if (saving) return;

    if (!isAdmin) {
      const message = getForbiddenMessage();
      setError(message);
      showToast(message, "error");
      return;
    }

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
      if (isForbiddenError(err)) {
        const message = getForbiddenMessage();
        setError(message);
        showToast(message, "error");
        return;
      }

      const data = isAxiosError<ApiProblemDetail>(err) ? err.response?.data : undefined;
      const { fieldErrors: apiErrors, fallbackMessage } = mapApiFieldErrors(data, EVENT_FIELD_NAMES);

      setFieldErrors(apiErrors);
      setError(fallbackMessage || "Não foi possível atualizar o evento.");
      showToast("Falha ao atualizar evento.", "error");
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = () => {
    if (!isAdmin) {
      const message = getForbiddenMessage();
      setError(message);
      showToast(message, "error");
      return;
    }

    setDeleteTarget({ type: "event", name: form.nome || "Evento sem nome" });
  };

  const closeDeleteDialog = () => {
    if (deleting || deletingSessionId !== null) return;
    setDeleteTarget(null);
  };

  const confirmDelete = async () => {
    if (!deleteTarget || deleting || deletingSessionId !== null) return;

    if (!isAdmin) {
      const message = getForbiddenMessage();
      setError(message);
      showToast(message, "error");
      return;
    }

    if (deleteTarget.type === "event") {
      setError(null);
      setDeleting(true);

      try {
        await excluirEvento(eventId);
        showToast("Evento excluído com sucesso.", "success");
        setDeleteTarget(null);
        router.push("/dashboard");
      } catch (err: unknown) {
        const message = isForbiddenError(err) ? getForbiddenMessage() : "Não foi possível excluir o evento.";
        setError(message);
        showToast(isForbiddenError(err) ? message : "Falha ao excluir evento.", "error");
      } finally {
        setDeleting(false);
      }

      return;
    }

    setDeletingSessionId(deleteTarget.id);
    let deletedSession = false;

    try {
      await excluirSessao(deleteTarget.id);
      showToast("Sessão removida.", "success");
      await sessions.reloadSessoes();
      deletedSession = true;
    } catch (err: unknown) {
      showToast(isForbiddenError(err) ? getForbiddenMessage() : "Erro ao remover sessão.", "error");
    } finally {
      setDeletingSessionId(null);
      if (deletedSession) {
        setDeleteTarget(null);
      }
    }
  };

  const resetSessionForm = () => {
    setSessaoNome("");
    setSessaoDataHora("");
    setSessaoStatus("ATIVO");
    setSessaoCapacidade("");
    setSessaoEditando(null);
    setSessaoFieldErrors({});
    setSessaoFormError(null);
  };

  const handleEditSession = (sessao: SessaoEventoResponse) => {
    const values = getSessionEditValues(sessao);

    setSessaoEditando(sessao);
    setSessaoNome(values.nome);
    setSessaoDataHora(values.dataHora);
    setSessaoStatus(values.status);
    setSessaoCapacidade(values.capacidade);
    setSessaoFieldErrors({});
    setSessaoFormError(null);
  };

  const handleSalvarSessao = async (event: FormEvent) => {
    event.preventDefault();
    if (!isAdmin) {
      const message = getForbiddenMessage();
      setSessaoFormError(message);
      showToast(message, "error");
      return;
    }

    setSessaoFormError(null);

    const errors = validateSessionForm(sessaoNome, sessaoDataHora, sessaoStatus, sessaoCapacidade);
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

      await sessions.reloadSessoes();
      resetSessionForm();
    } catch (err: unknown) {
      if (isForbiddenError(err)) {
        const message = getForbiddenMessage();
        setSessaoFormError(message);
        showToast(message, "error");
        return;
      }

      const data = isAxiosError<ApiProblemDetail>(err) ? err.response?.data : undefined;
      const { fieldErrors: apiErrors, fallbackMessage } = mapApiFieldErrors(data, SESSION_FIELD_NAMES);

      setSessaoFieldErrors(apiErrors);
      setSessaoFormError(fallbackMessage || "Não foi possível salvar a sessão.");
      showToast("Erro ao salvar sessão.", "error");
    }
  };

  const handleRemoverSessao = (sessao: SessaoEventoResponse) => {
    if (!isAdmin) {
      showToast(getForbiddenMessage(), "error");
      return;
    }

    setDeleteTarget({
      type: "session",
      id: sessao.idSessao,
      name: sessao.nomeSessao || `Sessão ${sessao.idSessao}`,
    });
  };

  const handleCriarTipo = async (event: FormEvent) => {
    event.preventDefault();
    if (!sessions.sessaoSelecionada) return;
    if (!isAdmin) {
      const message = getForbiddenMessage();
      setTipoFormError(message);
      showToast(message, "error");
      return;
    }

    setTipoFormError(null);

    const errors = validateTicketTypeForm(tipoNomeSetor, tipoPreco, tipoQuantidadeTotal, tipoLote);
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
      sessaoId: sessions.sessaoSelecionada,
    };

    try {
      await criarTipoIngresso(payload);
      showToast("Tipo de ingresso criado.", "success");
      await ticketTypes.reloadTipos();

      setTipoNomeSetor("");
      setTipoPreco("");
      setTipoQuantidadeTotal("");
      setTipoLote("");
      setTipoFieldErrors({});
      setTipoFormError(null);
    } catch (err: unknown) {
      if (isForbiddenError(err)) {
        const message = getForbiddenMessage();
        setTipoFormError(message);
        showToast(message, "error");
        return;
      }

      const data = isAxiosError<ApiProblemDetail>(err) ? err.response?.data : undefined;
      const { fieldErrors: apiErrors, fallbackMessage } = mapApiFieldErrors(data, TICKET_TYPE_FIELD_NAMES);

      setTipoFieldErrors(apiErrors);
      setTipoFormError(fallbackMessage || "Não foi possível criar o tipo de ingresso.");
      showToast("Erro ao criar tipo de ingresso.", "error");
    }
  };

  return {
    eventDetails,
    sessions,
    ticketTypes,
    saving,
    deleting,
    deletingSessionId,
    fieldErrors,
    deleteDialog: {
      target: deleteTarget,
      isOpen: deleteTarget !== null,
      isLoading: deleting || deletingSessionId !== null,
      close: closeDeleteDialog,
      confirm: confirmDelete,
    },
    eventHandlers: {
      updateField,
      handleSave,
      handleDelete,
      goBack: () => router.push("/dashboard"),
    },
    sessionForm: {
      nome: sessaoNome,
      dataHora: sessaoDataHora,
      status: sessaoStatus,
      capacidade: sessaoCapacidade,
      editando: sessaoEditando,
      fieldErrors: sessaoFieldErrors,
      formError: sessaoFormError,
    },
    sessionHandlers: {
      handleEditSession,
      handleSalvarSessao,
      handleRemoverSessao,
      setNome: (value: string) => {
        setSessaoNome(value);
        setSessaoFieldErrors((current) => ({ ...current, nomeSessao: undefined }));
        setSessaoFormError(null);
      },
      setDataHora: (value: string) => {
        setSessaoDataHora(value);
        setSessaoFieldErrors((current) => ({ ...current, dataHoraSessao: undefined }));
        setSessaoFormError(null);
      },
      setStatus: (value: SessionStatus) => {
        setSessaoStatus(value);
        setSessaoFieldErrors((current) => ({ ...current, statusSessao: undefined }));
        setSessaoFormError(null);
      },
      setCapacidade: (value: string) => {
        setSessaoCapacidade(value);
        setSessaoFieldErrors((current) => ({ ...current, capacidade: undefined }));
        setSessaoFormError(null);
      },
    },
    ticketTypeForm: {
      nomeSetor: tipoNomeSetor,
      preco: tipoPreco,
      quantidadeTotal: tipoQuantidadeTotal,
      lote: tipoLote,
      fieldErrors: tipoFieldErrors,
      formError: tipoFormError,
    },
    ticketTypeHandlers: {
      handleCriarTipo,
      setNomeSetor: (value: string) => {
        setTipoNomeSetor(value);
        setTipoFieldErrors((current) => ({ ...current, nomeSetor: undefined }));
        setTipoFormError(null);
      },
      setPreco: (value: string) => {
        setTipoPreco(value);
        setTipoFieldErrors((current) => ({ ...current, preco: undefined }));
        setTipoFormError(null);
      },
      setQuantidadeTotal: (value: string) => {
        setTipoQuantidadeTotal(value);
        setTipoFieldErrors((current) => ({ ...current, quantidadeTotal: undefined }));
        setTipoFormError(null);
      },
      setLote: (value: string) => {
        setTipoLote(value);
        setTipoFieldErrors((current) => ({ ...current, lote: undefined }));
        setTipoFormError(null);
      },
    },
  };
}
