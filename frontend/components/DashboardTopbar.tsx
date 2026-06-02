"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useEffect, useMemo, useState } from "react";
import styles from "@/app/dashboard/layout.module.css";
import { getMyProfile } from "@/services/auth";
import { UsuarioPerfil } from "@/services/types";

function getInitials(name: string): string {
    const parts = name.trim().split(/\s+/).filter(Boolean);
    if (parts.length === 0) return "";
    if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase();
    return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
}

export default function DashboardTopbar() {
    const router = useRouter();
    const [perfil, setPerfil] = useState<UsuarioPerfil | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const initials = useMemo(() => {
        if (!perfil?.nome) return "";
        return getInitials(perfil.nome);
    }, [perfil]);

    const handleLogout = () => {
        sessionStorage.removeItem("token");
        router.push("/");
    };

    useEffect(() => {
        let active = true;

        const loadProfile = async () => {
            try {
                setLoading(true);
                const data = await getMyProfile();
                if (active) {
                    setPerfil(data);
                    setError(null);
                }
            } catch {
                if (active) {
                    setError("Não foi possível carregar o perfil.");
                }
            } finally {
                if (active) setLoading(false);
            }
        };

        loadProfile();

        return () => {
            active = false;
        };
    }, []);

    return (
        <header className={styles.topbar}>
            <Link href="/dashboard" className={styles.brand}>
                <div className={styles.logoIcon}>…</div>
                TicketHub
            </Link>

            <div className={styles.profile}>
                {loading ? (
                    <span className={styles.userName}>Carregando…</span>
                ) : error ? (
                    <span className={styles.userName}>{error}</span>
                ) : (
                    <>
                        <div className={styles.avatar}>{initials}</div>
                        <span className={styles.userName}>{perfil?.nome}</span>
                    </>
                )}
                <button onClick={handleLogout} className={styles.logoutButton}>
                    Sair
                </button>
            </div>
        </header>
    );
}