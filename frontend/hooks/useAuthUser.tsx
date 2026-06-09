"use client";

import { createContext, useCallback, useContext, useEffect, useMemo, useState } from "react";
import { isAxiosError } from "axios";
import { getMyProfile } from "@/services/auth";
import { ApiProblemDetail, UsuarioAutenticado } from "@/services/types";

type AuthUserContextValue = {
  user: UsuarioAutenticado | null;
  loading: boolean;
  error: string | null;
  forbidden: boolean;
  isAdmin: boolean;
  reload: () => Promise<void>;
};

const AuthUserContext = createContext<AuthUserContextValue | null>(null);

function getProfileErrorMessage(err: unknown) {
  const data = isAxiosError<ApiProblemDetail>(err) ? err.response?.data : undefined;

  if (isAxiosError(err) && err.response?.status === 403) {
    return "Você não tem permissão para acessar este recurso.";
  }

  return data?.detail || "Não foi possível carregar o perfil.";
}

export function AuthUserProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<UsuarioAutenticado | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [forbidden, setForbidden] = useState(false);

  const loadUser = useCallback(async () => {
    setLoading(true);
    setError(null);
    setForbidden(false);

    try {
      const data = await getMyProfile();
      setUser(data);
    } catch (err: unknown) {
      setUser(null);
      setForbidden(isAxiosError(err) && err.response?.status === 403);
      setError(getProfileErrorMessage(err));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    let active = true;

    const load = async () => {
      setLoading(true);
      setError(null);
      setForbidden(false);

      try {
        const data = await getMyProfile();
        if (!active) return;
        setUser(data);
      } catch (err: unknown) {
        if (!active) return;
        setUser(null);
        setForbidden(isAxiosError(err) && err.response?.status === 403);
        setError(getProfileErrorMessage(err));
      } finally {
        if (active) setLoading(false);
      }
    };

    load();

    return () => {
      active = false;
    };
  }, []);

  const value = useMemo<AuthUserContextValue>(
    () => ({
      user,
      loading,
      error,
      forbidden,
      isAdmin: user?.role === "ADMINISTRADOR",
      reload: loadUser,
    }),
    [error, forbidden, loadUser, loading, user]
  );

  return <AuthUserContext.Provider value={value}>{children}</AuthUserContext.Provider>;
}

export function useAuthUser() {
  const context = useContext(AuthUserContext);
  if (!context) throw new Error("useAuthUser must be used within AuthUserProvider");
  return context;
}

export function usePermissions() {
  const { isAdmin, loading, user } = useAuthUser();
  return { isAdmin, loading, role: user?.role };
}
