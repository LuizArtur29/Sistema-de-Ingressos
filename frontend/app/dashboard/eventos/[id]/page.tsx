"use client";

import Link from "next/link";
import { useParams } from "next/navigation";
import ConfirmDialog from "@/components/ConfirmDialog";
import { Badge, Button, Card } from "@/components/ui";
import { usePermissions } from "@/hooks/useAuthUser";
import EventForm from "./_components/EventForm";
import SessionsManager from "./_components/SessionsManager";
import TicketTypesManager from "./_components/TicketTypesManager";
import { useEventDetailsController } from "./_hooks/useEventDetailsController";
import { EventoStatus } from "@/services/types";
import styles from "./page.module.css";

const statusLabelMap: Record<EventoStatus, string> = {
  ATIVO: "Ativo",
  CANCELADO: "Cancelado",
  FINALIZADO: "Finalizado",
};

const statusVariantMap: Record<EventoStatus, "success" | "danger" | "neutral"> = {
  ATIVO: "success",
  CANCELADO: "danger",
  FINALIZADO: "neutral",
};

export default function EventDetails() {
  const params = useParams<{ id: string }>();
  const { isAdmin } = usePermissions();
  const eventId = Number(params.id);
  const controller = useEventDetailsController(eventId, isAdmin);
  const { eventDetails, sessions, ticketTypes } = controller;
  const { form, loading, error } = eventDetails;

  if (loading) {
    return <p className={styles.feedback}>Carregando evento...</p>;
  }

  if (error && !form.nome) {
    return (
      <div className={styles.container}>
        <div className={styles.breadcrumbs}>
          <Link href="/dashboard">Eventos</Link> &gt; <span>Detalhes</span>
        </div>
        <Card className={styles.errorCard}>
          <p>{error}</p>
          <Button variant="secondary" onClick={controller.eventHandlers.goBack}>
            Voltar ao dashboard
          </Button>
        </Card>
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
          <Badge variant={statusVariantMap[form.status]}>
            {statusLabelMap[form.status]}
          </Badge>
          <h1 className={styles.title}>Detalhes do Evento</h1>
          <p className={styles.subtitle}>Visualize, edite ou exclua este evento.</p>
        </div>
        {isAdmin && (
          <Button
            type="button"
            variant="danger"
            onClick={controller.eventHandlers.handleDelete}
            disabled={controller.deleting || controller.saving}
          >
            {controller.deleting ? "Excluindo..." : "Excluir Evento"}
          </Button>
        )}
      </div>

      <EventForm
        form={form}
        fieldErrors={controller.fieldErrors}
        error={error}
        isAdmin={isAdmin}
        saving={controller.saving}
        deleting={controller.deleting}
        onCancel={controller.eventHandlers.goBack}
        onSubmit={controller.eventHandlers.handleSave}
        onFieldChange={controller.eventHandlers.updateField}
      />

      <SessionsManager
        sessoes={sessions.sessoes}
        loadingSessoes={sessions.loadingSessoes}
        errorSessoes={sessions.errorSessoes}
        isAdmin={isAdmin}
        form={controller.sessionForm}
        onEditSession={controller.sessionHandlers.handleEditSession}
        onRemoveSession={controller.sessionHandlers.handleRemoverSessao}
        onSubmitSession={controller.sessionHandlers.handleSalvarSessao}
        onSessionNameChange={controller.sessionHandlers.setNome}
        onSessionDateTimeChange={controller.sessionHandlers.setDataHora}
        onSessionStatusChange={controller.sessionHandlers.setStatus}
        onSessionCapacityChange={controller.sessionHandlers.setCapacidade}
        deletingSessionId={controller.deletingSessionId}
      />

      <TicketTypesManager
        sessoes={sessions.sessoes}
        sessaoSelecionada={sessions.sessaoSelecionada}
        tiposIngresso={ticketTypes.tiposIngresso}
        loadingTipos={ticketTypes.loadingTipos}
        errorTipos={ticketTypes.errorTipos}
        isAdmin={isAdmin}
        form={controller.ticketTypeForm}
        onSelectedSessionChange={sessions.setSessaoSelecionada}
        onSubmitTicketType={controller.ticketTypeHandlers.handleCriarTipo}
        onSectorChange={controller.ticketTypeHandlers.setNomeSetor}
        onPriceChange={controller.ticketTypeHandlers.setPreco}
        onQuantityChange={controller.ticketTypeHandlers.setQuantidadeTotal}
        onLotChange={controller.ticketTypeHandlers.setLote}
      />

      <ConfirmDialog
        open={controller.deleteDialog.isOpen}
        title={
          controller.deleteDialog.target?.type === "session"
            ? "Remover sessão?"
            : "Excluir evento?"
        }
        description={
          controller.deleteDialog.target?.type === "session"
            ? "Esta sessão será removida do evento. Essa ação não pode ser desfeita."
            : "Este evento será excluído permanentemente. Essa ação não pode ser desfeita."
        }
        resourceName={controller.deleteDialog.target?.name ?? ""}
        confirmLabel={controller.deleteDialog.target?.type === "session" ? "Remover sessão" : "Excluir evento"}
        loadingLabel={controller.deleteDialog.target?.type === "session" ? "Removendo..." : "Excluindo..."}
        isLoading={controller.deleteDialog.isLoading}
        onCancel={controller.deleteDialog.close}
        onConfirm={controller.deleteDialog.confirm}
      />
    </div>
  );
}
