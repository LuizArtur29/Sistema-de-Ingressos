"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { Button, EmptyState, PageHeader, StatusBadge, TextField, TicketCard } from "@/components/ui";
import { useToast } from "@/components/ToastProvider";
import { useAuthUser } from "@/hooks/useAuthUser";
import { listarComprasPorUsuario } from "@/services/compras";
import { transferirIngresso } from "@/services/transferencias";
import { CompraResponse } from "@/services/types";
import styles from "./page.module.css";

function formatMoney(value: number) {
  return value.toLocaleString("pt-BR", { style: "currency", currency: "BRL" });
}

function isConfirmed(status: string) {
  return status.toLowerCase().includes("conclu");
}

export default function MyTicketsPage() {
  const { user } = useAuthUser();
  const { showToast } = useToast();
  const [compras, setCompras] = useState<CompraResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [transferCompra, setTransferCompra] = useState<CompraResponse | null>(null);
  const [compradorId, setCompradorId] = useState("");
  const [valorRevenda, setValorRevenda] = useState("");
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (!user) return;
    const load = async () => {
      try {
        setCompras(await listarComprasPorUsuario(user.idUsuario));
      } catch {
        showToast("Não foi possível carregar seus ingressos.", "error");
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [showToast, user]);

  const closeTransfer = () => {
    setTransferCompra(null);
    setCompradorId("");
    setValorRevenda("");
  };

  const handleTransfer = async () => {
    if (!transferCompra) return;

    const ingressoId = transferCompra.ingressoId ?? transferCompra.ingressoIds?.[0];
    if (!ingressoId || !compradorId.trim()) return;

    setSubmitting(true);
    try {
      await transferirIngresso({
        ingressoId,
        compradorId: Number(compradorId),
        valorRevenda: Number(valorRevenda || 0),
      });
      showToast("Transferência realizada com sucesso.", "success");
      setCompras((prev) => prev.filter((c) => c.idCompra !== transferCompra.idCompra));
      closeTransfer();
    } catch {
      showToast("Não foi possível transferir o ingresso.", "error");
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return <p className={styles.feedback}>Carregando ingressos...</p>;
  }

  return (
    <div className={styles.page}>
      <PageHeader
        eyebrow="Meus eventos"
        title="Carteira de ingressos"
        subtitle="Visualize compras, status, QR Code e transfira titularidade quando necessário."
        actions={
          <Link href="/dashboard">
            <Button type="button" variant="secondary">Comprar mais ingressos</Button>
          </Link>
        }
      />

      {compras.length === 0 ? (
        <EmptyState
          title="Nenhum ingresso encontrado"
          description="Quando uma compra for concluída, seus ingressos aparecerão nesta carteira."
          action={
            <Link href="/dashboard">
              <Button type="button">Ver eventos</Button>
            </Link>
          }
        />
      ) : (
        <div className={styles.wallet}>
          {compras.map((compra) => {
            const confirmed = isConfirmed(compra.status);
            const ingressoId = compra.ingressoId ?? compra.ingressoIds?.[0];
            return (
              <TicketCard
                key={compra.idCompra}
                title={compra.nomeEvento ?? `Compra #${compra.idCompra}`}
                subtitle={`${compra.quantidadeIngressos} ingresso(s) · ${formatMoney(compra.valorTotal)}`}
                locked={!confirmed}
                status={
                  <StatusBadge status={confirmed ? "success" : "warning"}>
                    {confirmed ? "Confirmado" : compra.status}
                  </StatusBadge>
                }
                details={
                  <>
                    <span>Compra: #{compra.idCompra}</span>
                    <span>Ingresso: {ingressoId ?? "não informado"}</span>
                    <span>Pagamento: {compra.metodoPagamento}</span>
                  </>
                }
                action={
                  <Button type="button" variant="secondary" disabled={!ingressoId} onClick={() => setTransferCompra(compra)}>
                    Transferir ingresso
                  </Button>
                }
              />
            );
          })}
        </div>
      )}

      {transferCompra && (
        <div className={styles.modalBackdrop} role="dialog" aria-modal="true">
          <div className={styles.modal}>
            <h2>Transferir ingresso</h2>
            <p>Informe o ID do usuário destinatário e o valor de revenda.</p>
            <TextField
              id="transfer-buyer"
              label="ID do Usuário Destinatário"
              type="number"
              min="1"
              value={compradorId}
              onChange={(event) => setCompradorId(event.target.value)}
            />
            <TextField
              id="transfer-value"
              label="Valor de Revenda"
              type="number"
              min="0"
              step="0.01"
              value={valorRevenda}
              onChange={(event) => setValorRevenda(event.target.value)}
            />
            <div className={styles.modalActions}>
              <Button type="button" variant="secondary" onClick={closeTransfer} disabled={submitting}>Cancelar</Button>
              <Button type="button" onClick={handleTransfer} disabled={submitting || !compradorId.trim()}>
                {submitting ? "Transferindo..." : "Transferir"}
              </Button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
