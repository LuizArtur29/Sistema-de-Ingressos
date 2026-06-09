"use client";

import Link from "next/link";
import { useAuthUser } from "@/hooks/useAuthUser";
import styles from "@/app/dashboard/page.module.css";

export default function AdminGuard({ children }: { children: React.ReactNode }) {
  const { loading, error, isAdmin } = useAuthUser();

  if (loading) {
    return <p className={styles.resultsCount}>Verificando permissões...</p>;
  }

  if (error) {
    return <p className={styles.resultsCount}>{error}</p>;
  }

  if (!isAdmin) {
    return (
      <div className={styles.emptyCard}>
        <h1 className={styles.emptyTitle}>Acesso restrito</h1>
        <p className={styles.emptyText}>
          Você não tem permissão para acessar esta área administrativa.
        </p>
        <Link href="/dashboard" className={styles.button}>
          Voltar para eventos
        </Link>
      </div>
    );
  }

  return <>{children}</>;
}
