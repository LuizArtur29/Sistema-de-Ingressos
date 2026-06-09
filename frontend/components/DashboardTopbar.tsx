"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useMemo } from "react";
import styles from "@/app/dashboard/layout.module.css";
import { useAuthUser } from "@/hooks/useAuthUser";

function getInitials(name: string): string {
    const parts = name.trim().split(/\s+/).filter(Boolean);
    if (parts.length === 0) return "";
    if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase();
    return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
}

export default function DashboardTopbar() {
    const router = useRouter();
    const { user, loading, error, isAdmin } = useAuthUser();

    const initials = useMemo(() => {
        if (!user?.nome) return "";
        return getInitials(user.nome);
    }, [user]);

    const handleLogout = () => {
        sessionStorage.removeItem("token");
        router.push("/");
    };

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
                        <span className={styles.userName}>
                            {user?.nome}
                            {isAdmin ? " (Admin)" : ""}
                        </span>
                    </>
                )}
                <button onClick={handleLogout} className={styles.logoutButton}>
                    Sair
                </button>
            </div>
        </header>
    );
}
