import { useEffect, useState } from "react";
import { buscarEventoPorId } from "@/services/eventos";
import { EventFormState, initialEventFormState } from "../_utils/eventDetailsTypes";
import { toEventFormState } from "../_utils/dateHelpers";

export function useEventDetails(eventId: number, onLoadError: () => void) {
  const [form, setForm] = useState<EventFormState>(initialEventFormState);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!Number.isFinite(eventId)) {
      setError("Evento inválido.");
      setLoading(false);
      return;
    }

    let active = true;

    const load = async () => {
      setLoading(true);
      try {
        const evento = await buscarEventoPorId(eventId);
        if (!active) return;
        setForm(toEventFormState(evento));
        setError(null);
      } catch {
        if (!active) return;
        setError("Não foi possível carregar os dados do evento.");
        onLoadError();
      } finally {
        if (active) setLoading(false);
      }
    };

    load();

    return () => {
      active = false;
    };
  }, [eventId, onLoadError]);

  return { form, setForm, loading, error, setError };
}
