import { useCallback, useEffect, useState } from "react";
import { listarTiposPorSessao } from "@/services/tiposIngressos";
import { TipoIngressoResponse } from "@/services/types";

export function useTicketTypes(sessaoSelecionada: number | null, onLoadError: () => void) {
  const [tiposIngresso, setTiposIngresso] = useState<TipoIngressoResponse[]>([]);
  const [loadingTipos, setLoadingTipos] = useState(false);
  const [errorTipos, setErrorTipos] = useState<string | null>(null);

  const reloadTipos = useCallback(async () => {
    if (!sessaoSelecionada) {
      setTiposIngresso([]);
      return [];
    }

    const data = await listarTiposPorSessao(sessaoSelecionada);
    setTiposIngresso(data);
    return data;
  }, [sessaoSelecionada]);

  useEffect(() => {
    if (!sessaoSelecionada) {
      setTiposIngresso([]);
      return;
    }

    let active = true;

    const load = async () => {
      setLoadingTipos(true);
      setErrorTipos(null);

      try {
        const data = await listarTiposPorSessao(sessaoSelecionada);
        if (active) setTiposIngresso(data);
      } catch {
        if (!active) return;
        setErrorTipos("Não foi possível carregar os tipos de ingresso.");
        onLoadError();
      } finally {
        if (active) setLoadingTipos(false);
      }
    };

    load();

    return () => {
      active = false;
    };
  }, [onLoadError, sessaoSelecionada]);

  return {
    tiposIngresso,
    loadingTipos,
    errorTipos,
    reloadTipos,
  };
}
