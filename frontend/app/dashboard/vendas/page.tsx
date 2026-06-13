"use client";

import { useEffect, useMemo, useState } from "react";
import AdminGuard from "@/components/AdminGuard";
import { useToast } from "@/components/ToastProvider";
import { MetricCard, PageHeader, StatusBadge } from "@/components/ui";
import { listarCompras } from "@/services/compras";
import { CompraResponse } from "@/services/types";
import styles from "./page.module.css";

function formatMoney(value: number) {
  return value.toLocaleString("pt-BR", { style: "currency", currency: "BRL" });
}

export default function SalesPage() {
  const { showToast } = useToast();
  const [compras, setCompras] = useState<CompraResponse[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const load = async () => {
      try {
        setCompras(await listarCompras());
      } catch {
        showToast("Não foi possível carregar vendas.", "error");
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [showToast]);

  const stats = useMemo(() => {
    const receita = compras.reduce((sum, compra) => sum + compra.valorTotal, 0);
    const ingressos = compras.reduce((sum, compra) => sum + compra.quantidadeIngressos, 0);
    const concluidas = compras.filter((compra) => compra.status.toLowerCase().includes("conclu")).length;
    return { receita, ingressos, concluidas };
  }, [compras]);

  return (
    <AdminGuard>
      <div className={styles.page}>
        <PageHeader
          eyebrow="Acompanhamento"
          title="Vendas"
          subtitle="Lista consolidada de compras registradas, receita, quantidade de ingressos e status."
        />

        <div className={styles.stats}>
          <MetricCard label="Receita" value={formatMoney(stats.receita)} trend="valor total vendido" />
          <MetricCard label="Ingressos" value={stats.ingressos} trend="unidades compradas" />
          <MetricCard label="Compras" value={compras.length} trend={`${stats.concluidas} concluídas`} />
          <MetricCard label="Ticket médio" value={formatMoney(compras.length ? stats.receita / compras.length : 0)} />
        </div>

        {loading ? (
          <p className={styles.feedback}>Carregando vendas...</p>
        ) : (
          <div className={styles.tableWrap}>
            <table className={styles.table}>
              <thead>
                <tr>
                  <th>Compra</th>
                  <th>Evento</th>
                  <th>Cliente</th>
                  <th>Ingressos</th>
                  <th>Pagamento</th>
                  <th>Status</th>
                  <th>Total</th>
                </tr>
              </thead>
              <tbody>
                {compras.map((compra) => (
                  <tr key={compra.idCompra}>
                    <td>#{compra.idCompra}</td>
                    <td>
                      <div className={styles.eventName}>{compra.nomeEvento ?? "Evento não informado"}</div>
                      <span className={styles.muted}>{compra.dataCompra}</span>
                    </td>
                    <td>{compra.nomeUsuario}</td>
                    <td>{compra.quantidadeIngressos}</td>
                    <td>{compra.metodoPagamento}</td>
                    <td>
                      <StatusBadge status={compra.status.toLowerCase().includes("conclu") ? "success" : "warning"}>
                        {compra.status}
                      </StatusBadge>
                    </td>
                    <td>{formatMoney(compra.valorTotal)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </AdminGuard>
  );
}
