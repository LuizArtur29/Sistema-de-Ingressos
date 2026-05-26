"use client";

import { useEffect, useMemo, useState } from "react";
import Link from "next/link";
import { useToast } from "@/components/ToastProvider";
import { listarEventos } from "@/services/eventos";
import { Evento } from "@/services/types";
import styles from "./page.module.css";

type StatusClass = "active" | "canceled" | "finished";
type FiltroStatus = "TODOS" | "ATIVO" | "CANCELADO" | "FINALIZADO";

const statusClassMap: Record<Evento["status"], StatusClass> = {
  ATIVO: "active",
  CANCELADO: "canceled",
  FINALIZADO: "finished",
};

const statusLabelMap: Record<Evento["status"], string> = {
  ATIVO: "Ativo",
  CANCELADO: "Cancelado",
  FINALIZADO: "Finalizado",
};

function formatDate(value?: string) {
  if (!value) return "";
  const [year, month, day] = value.split("-");
  return `${day}/${month}/${year}`;
}

function formatDateRange(start?: string, end?: string) {
  if (!start) return "";
  if (!end || start === end) return formatDate(start);
  return `${formatDate(start)} - ${formatDate(end)}`;
}

export default function Dashboard() {
  const [eventos, setEventos] = useState<Evento[]>([]);
  const { showToast } = useToast();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [filtroStatus, setFiltroStatus] = useState<FiltroStatus>("TODOS");

  useEffect(() => {
    const load = async () => {
      try {
        const data = await listarEventos();
        setEventos(data);
      } catch {
        setError("Não foi possível carregar os eventos.");
        showToast("Erro ao carregar eventos.", "error");
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  const stats = useMemo(() => {
    const total = eventos.length;
    const ativos = eventos.filter((e) => e.status === "ATIVO").length;
    const cancelados = eventos.filter((e) => e.status === "CANCELADO").length;
    const finalizados = eventos.filter((e) => e.status === "FINALIZADO").length;
    return { total, ativos, cancelados, finalizados };
  }, [eventos]);

  const eventosFiltrados = useMemo(() => {
    if (filtroStatus === "TODOS") return eventos;
    return eventos.filter((e) => e.status === filtroStatus);
  }, [eventos, filtroStatus]);

  if (loading) {
    return <p className={styles.resultsCount}>Carregando eventos...</p>;
  }

  if (error) {
    return <p className={styles.resultsCount}>{error}</p>;
  }

  return (
      <div>
        <div className={styles.header}>
          <div>
            <h1 className={styles.title}>Meus Eventos</h1>
            <p className={styles.subtitle}>Gerencie e acompanhe todos os seus eventos.</p>
          </div>
          <div className={styles.actions}>
            <select
                className={styles.select}
                value={filtroStatus}
                onChange={(e) => setFiltroStatus(e.target.value as FiltroStatus)}
            >
              <option value="TODOS">Estado: Todos</option>
              <option value="ATIVO">Estado: Ativos</option>
              <option value="CANCELADO">Estado: Cancelados</option>
              <option value="FINALIZADO">Estado: Finalizados</option>
            </select>
            <Link href="/dashboard/create" className={styles.button}>
              + Criar Evento
            </Link>
          </div>
        </div>

        <div className={styles.statsGrid}>
          <div className={styles.statCard}>
            <div className={`${styles.statIcon} ${styles.blue}`}>
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M4 7V5c0-1.1.9-2 2-2h12c1.1 0 2 .9 2 2v2"/><path d="M20 17v2c0 1.1-.9 2-2 2H6c-1.1 0-2-.9-2-2v-2"/><path d="M4 12c1.1 0 2-.9 2-2 0-1.1-.9-2-2-2"/><path d="M20 12c-1.1 0-2-.9-2-2 0-1.1.9-2 2-2"/><circle cx="9" cy="12" r="1"/><circle cx="15" cy="12" r="1"/><line x1="12" y1="9" x2="12" y2="15"/></svg>
            </div>
            <div className={styles.statInfo}>
              <span className={styles.statValue}>{stats.total}</span>
              <span className={styles.statLabel}>Total de Eventos</span>
            </div>
          </div>

          <div className={styles.statCard}>
            <div className={`${styles.statIcon} ${styles.green}`}>
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><polyline points="22 7 13.5 15.5 8.5 10.5 2 17"/><polyline points="16 7 22 7 22 13"/></svg>
            </div>
            <div className={styles.statInfo}>
              <span className={styles.statValue}>{stats.ativos}</span>
              <span className={styles.statLabel}>Eventos Ativos</span>
            </div>
          </div>

          <div className={styles.statCard}>
            <div className={`${styles.statIcon} ${styles.red}`}>
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><circle cx="12" cy="12" r="10"/><path d="m15 9-6 6"/><path d="m9 9 6 6"/></svg>
            </div>
            <div className={styles.statInfo}>
              <span className={styles.statValue}>{stats.cancelados}</span>
              <span className={styles.statLabel}>Cancelados</span>
            </div>
          </div>

          <div className={styles.statCard}>
            <div className={`${styles.statIcon} ${styles.gray}`}>
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>
            </div>
            <div className={styles.statInfo}>
              <span className={styles.statValue}>{stats.finalizados}</span>
              <span className={styles.statLabel}>Finalizados</span>
            </div>
          </div>
        </div>

        <p className={styles.resultsCount}>{eventosFiltrados.length} eventos encontrados</p>

        <div className={styles.eventsGrid}>
          {eventosFiltrados.length === 0 ? (
              <div className={styles.emptyCard}>
                <h3 className={styles.emptyTitle}>Nenhum evento encontrado</h3>
                <p className={styles.emptyText}>
                  Você ainda não criou eventos. Clique em “Criar Evento” para começar.
                </p>
                <Link href="/dashboard/create" className={styles.button}>
                  + Criar Evento
                </Link>
              </div>
          ) : (
              eventosFiltrados.map((event) => (
                  <div key={event.id} className={styles.eventCard}>
                    <div className={styles.eventHeader}>
                      <h3 className={styles.eventTitle}>{event.nome}</h3>
                      <span className={`${styles.badge} ${styles[statusClassMap[event.status]]}`}>
                  {statusLabelMap[event.status]}
                </span>
                    </div>
                    <p className={styles.eventDesc}>{event.descricao}</p>
                    <div className={styles.eventDetails}>
                      <div className={styles.detailItem}>
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
                        {formatDateRange(event.dataInicio, event.dataFim)}
                      </div>
                      <div className={styles.detailItem}>
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0 1 16 0Z"/><circle cx="12" cy="10" r="3"/></svg>
                        {event.local}
                      </div>
                      <div className={styles.detailItem}>
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M22 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
                        {event.capacidadeTotal.toLocaleString("pt-BR")} pessoas
                      </div>
                    </div>
                  </div>
              ))
          )}
        </div>
      </div>
  );
}