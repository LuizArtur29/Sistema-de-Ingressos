import { useCallback, useEffect, useState } from "react";
import { listarSessoesPorEvento } from "@/services/sessoes";
import { SessaoEventoResponse } from "@/services/types";

export function useEventSessions(eventId: number, onLoadError: () => void) {
  const [sessoes, setSessoes] = useState<SessaoEventoResponse[]>([]);
  const [loadingSessoes, setLoadingSessoes] = useState(false);
  const [errorSessoes, setErrorSessoes] = useState<string | null>(null);
  const [sessaoSelecionada, setSessaoSelecionada] = useState<number | null>(null);

  const reloadSessoes = useCallback(async () => {
    const data = await listarSessoesPorEvento(eventId);
    setSessoes(data);
    setSessaoSelecionada((current) => {
      if (current && data.some((sessao) => sessao.idSessao === current)) return current;
      return data.length ? data[0].idSessao : null;
    });
    return data;
  }, [eventId]);

  useEffect(() => {
    if (!Number.isFinite(eventId)) return;

    let active = true;

    const load = async () => {
      setLoadingSessoes(true);
      setErrorSessoes(null);

      try {
        const data = await listarSessoesPorEvento(eventId);
        if (!active) return;
        setSessoes(data);
        if (data.length > 0) setSessaoSelecionada(data[0].idSessao);
      } catch {
        if (!active) return;
        setErrorSessoes("Não foi possível carregar as sessões.");
        onLoadError();
      } finally {
        if (active) setLoadingSessoes(false);
      }
    };

    load();

    return () => {
      active = false;
    };
  }, [eventId, onLoadError]);

  return {
    sessoes,
    setSessoes,
    loadingSessoes,
    errorSessoes,
    sessaoSelecionada,
    setSessaoSelecionada,
    reloadSessoes,
  };
}
