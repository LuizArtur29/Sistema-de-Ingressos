"use client";

import { useEffect, useMemo, useState } from "react";
import AdminGuard from "@/components/AdminGuard";
import { useToast } from "@/components/ToastProvider";
import { Button, MetricCard, PageHeader, SelectField, TextField } from "@/components/ui";
import { listarMeusEventos } from "@/services/eventos";
import { registrarEntrada } from "@/services/ingressos";
import { EventoResponse } from "@/services/types";
import styles from "./page.module.css";

export default function CheckinPage() {
  const { showToast } = useToast();
  const [eventos, setEventos] = useState<EventoResponse[]>([]);
  const [eventoId, setEventoId] = useState("");
  const [sessaoId, setSessaoId] = useState("");
  const [ingressoId, setIngressoId] = useState("");
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [validated, setValidated] = useState(0);
  const [lastResult, setLastResult] = useState<string | null>(null);

  useEffect(() => {
    const load = async () => {
      try {
        const data = await listarMeusEventos();
        setEventos(data);
        if (data[0]) {
          setEventoId(String(data[0].id));
          if (data[0].sessoes?.[0]) setSessaoId(String(data[0].sessoes[0].idSessao));
        }
      } catch {
        showToast("Não foi possível carregar eventos para check-in.", "error");
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [showToast]);

  const selectedEvent = useMemo(
    () => eventos.find((evento) => String(evento.id) === eventoId) ?? null,
    [eventoId, eventos]
  );

  const handleEventChange = (value: string) => {
    setEventoId(value);
    const nextEvent = eventos.find((evento) => String(evento.id) === value);
    setSessaoId(nextEvent?.sessoes?.[0] ? String(nextEvent.sessoes[0].idSessao) : "");
  };

  const handleSubmit = async () => {
    if (!ingressoId.trim()) return;
    setSubmitting(true);
    try {
      await registrarEntrada(Number(ingressoId));
      setValidated((current) => current + 1);
      setLastResult(`Ingresso #${ingressoId} validado com sucesso.`);
      setIngressoId("");
      showToast("Entrada registrada.", "success");
    } catch {
      showToast("Não foi possível registrar entrada.", "error");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <AdminGuard>
      <div className={styles.page}>
        <PageHeader
          eyebrow="Entrada de ingressos"
          title="Check-in da sessão"
          subtitle="Registre entrada por leitura de QR Code ou validação manual do ID do ingresso."
        />

        {loading ? (
          <p className={styles.feedback}>Carregando eventos...</p>
        ) : (
          <div className={styles.checkinBox}>
            <div className={styles.scanner}>
              <strong>Escanear QR Code</strong>
            </div>

            <aside className={styles.panel}>
              <h2>Validação manual</h2>
              <SelectField
                id="checkin-event"
                label="Evento"
                value={eventoId}
                onChange={(event) => handleEventChange(event.target.value)}
              >
                {eventos.map((evento) => (
                  <option key={evento.id} value={evento.id}>
                    {evento.nome}
                  </option>
                ))}
              </SelectField>

              <SelectField
                id="checkin-session"
                label="Sessão"
                value={sessaoId}
                onChange={(event) => setSessaoId(event.target.value)}
              >
                {selectedEvent?.sessoes?.map((sessao) => (
                  <option key={sessao.idSessao} value={sessao.idSessao}>
                    {sessao.nomeSessao}
                  </option>
                ))}
              </SelectField>

              <TextField
                id="checkin-ticket"
                label="Código do ingresso"
                type="number"
                min="1"
                placeholder="Ex.: 931"
                value={ingressoId}
                onChange={(event) => setIngressoId(event.target.value)}
              />

              <Button type="button" fullWidth disabled={submitting || !ingressoId.trim()} onClick={handleSubmit}>
                {submitting ? "Registrando..." : "Registrar entrada"}
              </Button>

              <div className={styles.stats}>
                <MetricCard label="Validados" value={validated} />
                <MetricCard label="Sessões" value={selectedEvent?.sessoes?.length ?? 0} />
              </div>

              {lastResult && <div className={styles.lastResult}>{lastResult}</div>}
            </aside>
          </div>
        )}
      </div>
    </AdminGuard>
  );
}
