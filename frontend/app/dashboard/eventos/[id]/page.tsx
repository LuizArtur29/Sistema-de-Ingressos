"use client";

import { useEffect, useMemo, useState } from "react";
import Link from "next/link";
import { useParams, useRouter } from "next/navigation";
import ConfirmDialog from "@/components/ConfirmDialog";
import { Badge, Button, Card, EmptyState, MetricCard, PageHeader, SelectField, StatusBadge } from "@/components/ui";
import { useAuthUser, usePermissions } from "@/hooks/useAuthUser";
import { realizarCompra } from "@/services/compras";
import { listarIngressosPorSessao } from "@/services/ingressos";
import { EventoStatus, IngressoResponse } from "@/services/types";
import EventForm from "./_components/EventForm";
import SessionsManager from "./_components/SessionsManager";
import TicketTypesManager from "./_components/TicketTypesManager";
import { useEventDetailsController } from "./_hooks/useEventDetailsController";
import styles from "./page.module.css";
import { useToast } from "@/components/ToastProvider";

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

function formatDate(value?: string) {
  if (!value) return "";
  return new Date(value).toLocaleString("pt-BR");
}

function formatMoney(value: number) {
  return value.toLocaleString("pt-BR", { style: "currency", currency: "BRL" });
}

export default function EventDetails() {
  const params = useParams<{ id: string }>();
  const router = useRouter();
  const { showToast } = useToast();
  const { isAdmin } = usePermissions();
  const { user } = useAuthUser();
  const eventId = Number(params.id);
  const controller = useEventDetailsController(eventId, isAdmin);
  const { eventDetails, sessions, ticketTypes } = controller;
  const { form, loading, error } = eventDetails;
  const [ingressos, setIngressos] = useState<IngressoResponse[]>([]);
  const [loadingIngressos, setLoadingIngressos] = useState(false);
  const [selectedTypeId, setSelectedTypeId] = useState<number | null>(null);
  const [quantidade, setQuantidade] = useState(1);
  const [metodoPagamento, setMetodoPagamento] = useState<"PIX" | "CARTAO_CREDITO" | "BOLETO">("PIX");
  const [meiaEntrada, setMeiaEntrada] = useState(false);
  const [buying, setBuying] = useState(false);

  useEffect(() => {
    const sessaoSelecionada = sessions.sessaoSelecionada;
    if (!sessaoSelecionada) {
      setIngressos([]);
      return;
    }

    let active = true;
    const load = async () => {
      setLoadingIngressos(true);
      try {
        const data = await listarIngressosPorSessao(sessaoSelecionada);
        if (active) setIngressos(data);
      } catch {
        if (active) showToast("Não foi possível carregar ingressos disponíveis.", "error");
      } finally {
        if (active) setLoadingIngressos(false);
      }
    };
    load();
    return () => {
      active = false;
    };
  }, [sessions.sessaoSelecionada, showToast]);

  useEffect(() => {
    if (ticketTypes.tiposIngresso.length === 0) {
      setSelectedTypeId(null);
      return;
    }
    setSelectedTypeId((current) =>
      current && ticketTypes.tiposIngresso.some((tipo) => tipo.idTipoIngresso === current)
        ? current
        : ticketTypes.tiposIngresso[0].idTipoIngresso
    );
  }, [ticketTypes.tiposIngresso]);

  const selectedType = useMemo(
    () => ticketTypes.tiposIngresso.find((tipo) => tipo.idTipoIngresso === selectedTypeId) ?? null,
    [selectedTypeId, ticketTypes.tiposIngresso]
  );

  const ingressoBase = useMemo(
    () =>
      ingressos.find(
        (ingresso) =>
          ingresso.disponivelParaCompra &&
          ingresso.ingressoDisponivel &&
          !ingresso.vendido &&
          ingresso.idTipoIngresso === selectedTypeId
      ) ?? null,
    [ingressos, selectedTypeId]
  );

  const maxQuantidade = Math.max(1, selectedType?.quantidadeDisponivel ?? 1);
  const subtotal = (selectedType?.preco ?? 0) * quantidade * (meiaEntrada ? 0.5 : 1);
  const taxa = subtotal > 0 ? subtotal * 0.05 : 0;

  const handleBuy = async () => {
    if (!user || !ingressoBase) return;
    setBuying(true);
    try {
      await realizarCompra({
        usuarioID: user.idUsuario,
        ingressoID: ingressoBase.idIngresso,
        quantidadeIngressos: quantidade,
        metodoPagamento,
        isMeiaEntrada: meiaEntrada,
      });
      showToast("Compra realizada com sucesso.", "success");
      router.push("/dashboard/ingressos");
    } catch {
      showToast("Não foi possível concluir a compra.", "error");
    } finally {
      setBuying(false);
    }
  };

  if (loading) {
    return <p className={styles.feedback}>Carregando evento...</p>;
  }

  if (error && !form.nome) {
    return (
      <Card className={styles.errorCard}>
        <p>{error}</p>
        <Button variant="secondary" onClick={controller.eventHandlers.goBack}>
          Voltar ao dashboard
        </Button>
      </Card>
    );
  }

  return (
    <div className={styles.container}>
      <Link href="/dashboard" className={styles.backLink}>
        ← Voltar para eventos
      </Link>

      <div className={styles.detailGrid}>
        <main className={styles.mainColumn}>
          <section className={styles.hero}>
            <Badge variant={statusVariantMap[form.status]}>{statusLabelMap[form.status]}</Badge>
            <h1>{form.nome}</h1>
            <p>{form.descricao}</p>
          </section>

          <div className={styles.stats}>
            <MetricCard label="Local" value={form.local || "-"} />
            <MetricCard label="Início" value={form.dataInicio || "-"} />
            <MetricCard label="Capacidade" value={form.capacidadeTotal || "-"} />
          </div>

          {isAdmin ? (
            <>
              <PageHeader
                eyebrow="Gerenciamento"
                title="Editar evento"
                subtitle="Atualize informações principais, sessões e lotes sem sair do contexto do evento."
                actions={
                  <Button
                    type="button"
                    variant="danger"
                    onClick={controller.eventHandlers.handleDelete}
                    disabled={controller.deleting || controller.saving}
                  >
                    {controller.deleting ? "Excluindo..." : "Excluir evento"}
                  </Button>
                }
              />

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
            </>
          ) : (
            <Card title="Sobre o evento" subtitle="Informações principais para planejar sua compra.">
              <p className={styles.aboutText}>{form.descricao}</p>
            </Card>
          )}
        </main>

        {!isAdmin && (
          <aside className={styles.purchasePanel}>
            <h2>Comprar ingresso</h2>
            <p>Escolha uma sessão, o tipo de ingresso e confirme o resumo do pedido.</p>

            {sessions.sessoes.length === 0 ? (
              <EmptyState title="Sem sessões" description="Este evento ainda não possui sessões disponíveis para compra." />
            ) : (
              <>
                <div className={styles.optionList}>
                  {sessions.sessoes.map((sessao) => (
                    <button
                      key={sessao.idSessao}
                      type="button"
                      className={`${styles.option} ${sessions.sessaoSelecionada === sessao.idSessao ? styles.optionSelected : ""}`}
                      onClick={() => sessions.setSessaoSelecionada(sessao.idSessao)}
                    >
                      <span>
                        <strong>{sessao.nomeSessao}</strong>
                        <small>{formatDate(sessao.dataHoraSessao)}</small>
                      </span>
                      <StatusBadge status={sessao.statusSessao === "ATIVO" ? "success" : "warning"}>{sessao.statusSessao}</StatusBadge>
                    </button>
                  ))}
                </div>

                <div className={styles.divider} />

                <div className={styles.optionList}>
                  {ticketTypes.loadingTipos || loadingIngressos ? (
                    <p className={styles.feedback}>Carregando lotes...</p>
                  ) : ticketTypes.tiposIngresso.length === 0 ? (
                    <p className={styles.feedback}>Nenhum lote disponível para esta sessão.</p>
                  ) : (
                    ticketTypes.tiposIngresso.map((tipo) => (
                      <button
                        key={tipo.idTipoIngresso}
                        type="button"
                        className={`${styles.option} ${selectedTypeId === tipo.idTipoIngresso ? styles.optionSelected : ""}`}
                        onClick={() => {
                          setSelectedTypeId(tipo.idTipoIngresso);
                          setQuantidade(1);
                        }}
                      >
                        <span>
                          <strong>{tipo.nomeSetor} · Lote {tipo.lote}</strong>
                          <small>{tipo.quantidadeDisponivel} disponíveis</small>
                        </span>
                        <b>{formatMoney(tipo.preco)}</b>
                      </button>
                    ))
                  )}
                </div>

                <div className={styles.divider} />

                <div className={styles.summaryRow}>
                  <span>Quantidade</span>
                  <div className={styles.qty}>
                    <button type="button" onClick={() => setQuantidade((value) => Math.max(1, value - 1))}>-</button>
                    <strong>{quantidade}</strong>
                    <button type="button" onClick={() => setQuantidade((value) => Math.min(maxQuantidade, value + 1))}>+</button>
                  </div>
                </div>

                <label className={styles.checkbox}>
                  <input type="checkbox" checked={meiaEntrada} onChange={(event) => setMeiaEntrada(event.target.checked)} />
                  Aplicar meia-entrada
                </label>

                <SelectField
                  id="payment-method"
                  label="Pagamento"
                  value={metodoPagamento}
                  onChange={(event) => setMetodoPagamento(event.target.value as typeof metodoPagamento)}
                >
                  <option value="PIX">PIX</option>
                  <option value="CARTAO_CREDITO">Cartão de crédito</option>
                  <option value="BOLETO">Boleto</option>
                </SelectField>

                <div className={styles.summaryRow}><span>Subtotal</span><strong>{formatMoney(subtotal)}</strong></div>
                <div className={styles.summaryRow}><span>Taxa</span><strong>{formatMoney(taxa)}</strong></div>
                <div className={styles.summaryTotal}><span>Total</span><strong>{formatMoney(subtotal + taxa)}</strong></div>

                <Button type="button" fullWidth disabled={!ingressoBase || buying || form.status !== "ATIVO"} onClick={handleBuy}>
                  {buying ? "Comprando..." : "Comprar agora"}
                </Button>
              </>
            )}
          </aside>
        )}
      </div>

      <ConfirmDialog
        open={controller.deleteDialog.isOpen}
        title={controller.deleteDialog.target?.type === "session" ? "Remover sessão?" : "Excluir evento?"}
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
