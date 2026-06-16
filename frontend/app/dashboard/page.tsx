"use client";

import { Suspense, useEffect, useMemo, useState } from "react";
import Link from "next/link";
import { useRouter, useSearchParams } from "next/navigation";
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
  SelectField,
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

function DashboardContent() {
  const router = useRouter();
  const searchParams = useSearchParams();

  const [eventosDisponiveis, setEventosDisponiveis] = useState<EventoResponse[]>([]);
  const [meusEventos, setMeusEventos] = useState<EventoResponse[]>([]);
  
  const [scope, setScope] = useState<"disponiveis" | "meus">((searchParams.get("scope") as "disponiveis" | "meus") || "disponiveis");
  const [filtroStatus, setFiltroStatus] = useState<FiltroStatus>((searchParams.get("status") as FiltroStatus) || "TODOS");
  const [busca, setBusca] = useState(searchParams.get("q") || "");
  const [ordenacao, setOrdenacao] = useState(searchParams.get("sort") || "DATA_ASC");
  const [pagina, setPagina] = useState(Number(searchParams.get("page")) || 1);
  const itensPorPagina = 6;

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

  useEffect(() => {
    const params = new URLSearchParams();
    if (scope !== "disponiveis") params.set("scope", scope);
    if (filtroStatus !== "TODOS") params.set("status", filtroStatus);
    if (busca) params.set("q", busca);
    if (ordenacao !== "DATA_ASC") params.set("sort", ordenacao);
    if (pagina > 1) params.set("page", pagina.toString());
    
    const newQuery = params.toString();
    router.replace(`/dashboard${newQuery ? `?${newQuery}` : ""}`, { scroll: false });
  }, [scope, filtroStatus, busca, ordenacao, pagina, router]);

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
    const filtrados = list.filter((event) => {
      const matchesStatus =
        scope === "disponiveis" ? event.status === "ATIVO" : filtroStatus === "TODOS" || event.status === filtroStatus;
      const searchable = normalize(`${event.nome} ${event.descricao || ""} ${event.local || ""}`);
      return matchesStatus && (!termo || searchable.includes(termo));
    });

    filtrados.sort((a, b) => {
      switch (ordenacao) {
        case "DATA_ASC":
          return new Date(a.dataInicio || 0).getTime() - new Date(b.dataInicio || 0).getTime();
        case "DATA_DESC":
          return new Date(b.dataInicio || 0).getTime() - new Date(a.dataInicio || 0).getTime();
        case "NOME_ASC":
          return a.nome.localeCompare(b.nome);
        case "NOME_DESC":
          return b.nome.localeCompare(a.nome);
        case "STATUS":
          return a.status.localeCompare(b.status);
        default:
          return 0;
      }
    });

    return filtrados;
  }, [busca, filtroStatus, list, scope, ordenacao]);

  const totalPaginas = Math.max(1, Math.ceil(eventosFiltrados.length / itensPorPagina));
  
  useEffect(() => {
    if (pagina > totalPaginas && totalPaginas > 0) setPagina(totalPaginas);
  }, [totalPaginas, pagina]);

  const eventosPaginados = eventosFiltrados.slice((pagina - 1) * itensPorPagina, pagina * itensPorPagina);

  const handleBuscaChange = (val: string) => { setBusca(val); setPagina(1); };
  const handleScopeChange = (val: "disponiveis" | "meus") => { setScope(val); setPagina(1); };
  const handleFiltroStatusChange = (val: FiltroStatus) => { setFiltroStatus(val); setPagina(1); };
  const handleOrdenacaoChange = (val: string) => { setOrdenacao(val); setPagina(1); };

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
          isAdmin ? (
            <Link href="/dashboard/create">
              <Button type="button">+ Novo evento</Button>
            </Link>
          ) : (
            <div style={{ width: '145px' }} />
          )
        }
      />

      <div className={styles.stats}>
        <MetricCard label={isAdmin ? "Eventos criados" : "Eventos ativos"} value={stats.total} trend={`${stats.ativos} ativos`} />
        <MetricCard label="Sessões" value={stats.sessoes} trend="programação publicada" />
        <MetricCard label="Capacidade" value={stats.capacidade.toLocaleString("pt-BR")} trend="lugares totais" />
        <MetricCard label={isAdmin ? "Check-in" : "Menor fluxo"} value={isAdmin ? "Pronto" : "Compra"} trend="sem troca de tela" />
      </div>

      <div className={styles.toolbar} style={{ 
        display: 'flex', 
        justifyContent: 'space-between', 
        alignItems: 'flex-end', 
        flexWrap: 'wrap', 
        gap: '2rem',
        padding: '1.5rem',
        backgroundColor: '#f9fafb',
        borderRadius: '0.75rem',
        border: '1px solid #e5e7eb',
        marginBottom: '2rem'
      }}>
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem', minWidth: '320px' }}>
          <FilterTabs
            value={scope}
            onChange={handleScopeChange}
            items={[
              { value: "disponiveis", label: "Eventos disponíveis" },
              { value: "meus", label: isAdmin ? "Meus eventos" : "Eventos comprados" },
            ]}
          />
          <div style={{ minHeight: '36px', display: 'flex', alignItems: 'center' }}>
            <div style={{ 
              opacity: scope === "meus" ? 1 : 0, 
              pointerEvents: scope === "meus" ? 'auto' : 'none', 
              transform: scope === "meus" ? 'translateY(0)' : 'translateY(-5px)',
              transition: 'all 0.2s ease-in-out' 
            }}>
              <FilterTabs
                value={filtroStatus}
                onChange={handleFiltroStatusChange}
                items={[
                  { value: "TODOS", label: "Todos" },
                  { value: "ATIVO", label: "Ativos" },
                  { value: "CANCELADO", label: "Cancelados" },
                  { value: "FINALIZADO", label: "Finalizados" },
                ]}
              />
            </div>
          </div>
        </div>
        
        <div style={{ display: 'flex', gap: '1rem', alignItems: 'flex-end', flexWrap: 'wrap', flexGrow: 1, justifyContent: 'flex-end' }}>
          <div style={{ flexGrow: 1, maxWidth: '400px', minWidth: '250px' }}>
            <div style={{ fontSize: '0.875rem', fontWeight: 500, marginBottom: '0.5rem', color: '#374151' }}>Buscar evento</div>
            <SearchBar value={busca} onChange={handleBuscaChange} placeholder="Nome, descrição ou local..." />
          </div>
          
          <div style={{ width: '220px' }}>
            <SelectField label="Ordenar por" value={ordenacao} onChange={(e) => handleOrdenacaoChange(e.target.value)}>
              <option value="DATA_ASC">Data (Crescente)</option>
              <option value="DATA_DESC">Data (Decrescente)</option>
              <option value="NOME_ASC">Nome (A-Z)</option>
              <option value="NOME_DESC">Nome (Z-A)</option>
              <option value="STATUS">Status</option>
            </SelectField>
          </div>
        </div>
      </div>

      <p className={styles.resultsCount}>{eventosFiltrados.length} eventos encontrados</p>

      {list.length === 0 ? (
        <EmptyState
          title={isAdmin && scope === "meus" ? "Nenhum evento criado" : "Nenhum evento disponível"}
          description={
             isAdmin && scope === "meus"
                ? "Você ainda não criou nenhum evento no sistema."
                : "Não há eventos disponíveis na plataforma no momento."
          }
          action={
            isAdmin && scope === "meus" ? (
              <Link href="/dashboard/create">
                <Button type="button">Criar evento</Button>
              </Link>
            ) : undefined
          }
        />
      ) : eventosFiltrados.length === 0 ? (
        <EmptyState
          title="Nenhum resultado encontrado"
          description="Nenhum evento corresponde aos filtros aplicados. Tente usar outros termos ou remover os filtros."
          action={
            <Button type="button" onClick={() => { setBusca(""); setFiltroStatus("TODOS"); setPagina(1); }}>
              Limpar filtros
            </Button>
          }
        />
      ) : (
        <div className={styles.eventsGrid}>
          {eventosPaginados.map((event, index) => (
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

      {eventosFiltrados.length > 0 && totalPaginas > 1 && (
        <div style={{ display: 'flex', justifyContent: 'center', gap: '1rem', marginTop: '2rem' }}>
          <Button disabled={pagina === 1} onClick={() => setPagina(p => Math.max(1, p - 1))} variant="secondary">Anterior</Button>
          <span style={{ display: 'flex', alignItems: 'center' }}>Página {pagina} de {totalPaginas}</span>
          <Button disabled={pagina === totalPaginas} onClick={() => setPagina(p => Math.min(totalPaginas, p + 1))} variant="secondary">Próxima</Button>
        </div>
      )}
    </div>
  );
}

export default function Dashboard() {
  return (
    <Suspense fallback={<p>Carregando painel...</p>}>
      <DashboardContent />
    </Suspense>
  );
}
