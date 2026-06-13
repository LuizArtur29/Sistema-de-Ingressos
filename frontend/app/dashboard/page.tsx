"use client";

import { useEffect, useMemo, useState } from "react";
import Link from "next/link";
import { useToast } from "@/components/ToastProvider";
import { usePermissions } from "@/hooks/useAuthUser";
import { listarEventos, listarMeusEventos } from "@/services/eventos";
import { EventoResponse, EventoStatus } from "@/services/types";
import {
  Button,
  EmptyState,
  EventCard,
  FilterTabs,
  MetricCard,
  PageHeader,
  SearchBar,
  StatusBadge,
} from "@/components/ui";
import styles from "./page.module.css";

type FiltroStatus = "TODOS" | EventoStatus;

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

function normalize(value: string) {
  return value
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .toLowerCase();
}

function formatDate(value?: string) {
  if (!value) return "";
  const [year, month, day] = value.split("-");
  return `${day}/${month}/${year}`;
}

function percentSold(event: EventoResponse) {
  const totalSessions = event.sessoes?.length || 0;
  if (!event.capacidadeTotal || !totalSessions) return 0;
  return Math.min(88, Math.max(12, totalSessions * 18));
}

export default function Dashboard() {
  const [eventosDisponiveis, setEventosDisponiveis] = useState<EventoResponse[]>([]);
  const [meusEventos, setMeusEventos] = useState<EventoResponse[]>([]);
  const [scope, setScope] = useState<"disponiveis" | "meus">("disponiveis");
  const [filtroStatus, setFiltroStatus] = useState<FiltroStatus>("TODOS");
  const [busca, setBusca] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { showToast } = useToast();
  const { isAdmin } = usePermissions();

  useEffect(() => {
    const load = async () => {
      try {
        const [disponiveisData, meusData] = await Promise.all([listarEventos(), listarMeusEventos()]);
        setEventosDisponiveis(disponiveisData);
        setMeusEventos(meusData);
      } catch {
        setError("Não foi possível carregar os eventos.");
        showToast("Erro ao carregar eventos.", "error");
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [showToast]);

  const list = scope === "disponiveis" ? eventosDisponiveis : meusEventos;

  const stats = useMemo(() => {
    const base = isAdmin ? meusEventos : eventosDisponiveis;
    const ativos = base.filter((event) => event.status === "ATIVO").length;
    const sessoes = base.reduce((sum, event) => sum + (event.sessoes?.length || 0), 0);
    const capacidade = base.reduce((sum, event) => sum + (event.capacidadeTotal || 0), 0);
    return { total: base.length, ativos, sessoes, capacidade };
  }, [eventosDisponiveis, isAdmin, meusEventos]);

  const eventosFiltrados = useMemo(() => {
    const termo = normalize(busca.trim());
    return list.filter((event) => {
      const matchesStatus =
        scope === "disponiveis" ? event.status === "ATIVO" : filtroStatus === "TODOS" || event.status === filtroStatus;
      const searchable = normalize(`${event.nome} ${event.descricao} ${event.local}`);
      return matchesStatus && (!termo || searchable.includes(termo));
    });
  }, [busca, filtroStatus, list, scope]);

  if (loading) {
    return <p className={styles.feedback}>Carregando eventos...</p>;
  }

  if (error) {
    return <p className={styles.feedback}>{error}</p>;
  }

  return (
    <div className={styles.page}>
      <PageHeader
        eyebrow={isAdmin ? "Painel administrativo" : "Eventos disponíveis"}
        title={isAdmin ? "Visão geral" : "Encontre seu próximo evento"}
        subtitle={
          isAdmin
            ? "Acompanhe seus eventos, sessões, lotes e capacidade em uma visão organizada."
            : "Explore eventos ativos, filtre por interesse e avance para a compra de ingressos."
        }
        actions={
          <>
            <SearchBar value={busca} onChange={setBusca} placeholder="Buscar evento ou local..." />
            {isAdmin && (
              <Link href="/dashboard/create">
                <Button type="button">+ Novo evento</Button>
              </Link>
            )}
          </>
        }
      />

      <div className={styles.stats}>
        <MetricCard label={isAdmin ? "Eventos criados" : "Eventos ativos"} value={stats.total} trend={`${stats.ativos} ativos`} />
        <MetricCard label="Sessões" value={stats.sessoes} trend="programação publicada" />
        <MetricCard label="Capacidade" value={stats.capacidade.toLocaleString("pt-BR")} trend="lugares totais" />
        <MetricCard label={isAdmin ? "Check-in" : "Menor fluxo"} value={isAdmin ? "Pronto" : "Compra"} trend="sem troca de tela" />
      </div>

      <div className={styles.toolbar}>
        <FilterTabs
          value={scope}
          onChange={setScope}
          items={[
            { value: "disponiveis", label: "Eventos disponíveis" },
            { value: "meus", label: isAdmin ? "Meus eventos" : "Eventos comprados" },
          ]}
        />
        {scope === "meus" && (
          <FilterTabs
            value={filtroStatus}
            onChange={setFiltroStatus}
            items={[
              { value: "TODOS", label: "Todos" },
              { value: "ATIVO", label: "Ativos" },
              { value: "CANCELADO", label: "Cancelados" },
              { value: "FINALIZADO", label: "Finalizados" },
            ]}
          />
        )}
      </div>

      <p className={styles.resultsCount}>{eventosFiltrados.length} eventos encontrados</p>

      {eventosFiltrados.length === 0 ? (
        <EmptyState
          title="Nenhum evento encontrado"
          description={
            scope === "disponiveis"
              ? "Não há eventos ativos que correspondam à busca atual."
              : isAdmin
                ? "Você ainda não criou eventos com esse filtro."
                : "Suas compras aparecerão em Meus ingressos assim que forem registradas."
          }
          action={
            isAdmin && scope === "meus" ? (
              <Link href="/dashboard/create">
                <Button type="button">Criar evento</Button>
              </Link>
            ) : undefined
          }
        />
      ) : (
        <div className={styles.eventsGrid}>
          {eventosFiltrados.map((event, index) => (
            <EventCard
              key={event.id}
              href={`/dashboard/eventos/${event.id}`}
              title={event.nome}
              description={event.descricao}
              date={event.dataInicio}
              location={event.local}
              capacity={`${event.capacidadeTotal.toLocaleString("pt-BR")} pessoas`}
              status={<StatusBadge status={statusVariantMap[event.status]}>{statusLabelMap[event.status]}</StatusBadge>}
              tone={index}
              footer={
                <>
                  <div className={styles.price}>
                    <small>{event.sessoes?.length || 0} sessões</small>
                    {formatDate(event.dataInicio)}
                  </div>
                  <span className={styles.progressText}>{percentSold(event)}% estruturado</span>
                </>
              }
            />
          ))}
        </div>
      )}
    </div>
  );
}
