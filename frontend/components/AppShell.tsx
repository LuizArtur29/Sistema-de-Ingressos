"use client";

import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { useMemo } from "react";
import { useAuthUser } from "@/hooks/useAuthUser";
import { clearToken } from "@/lib/authToken";
import AdminSidebar from "./AdminSidebar";
import styles from "./AppShell.module.css";

function getInitials(name: string): string {
  const parts = name.trim().split(/\s+/).filter(Boolean);
  if (parts.length === 0) return "";
  if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase();
  return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
}

function UserTopbar() {
  const router = useRouter();
  const pathname = usePathname();
  const { user, loading, error, isAdmin } = useAuthUser();

  const initials = useMemo(() => (user?.nome ? getInitials(user.nome) : ""), [user]);

  const handleLogout = () => {
    clearToken();
    router.push("/");
  };

  return (
    <header className={styles.topbar}>
      <div className={styles.topbarInner}>
        <Link href="/dashboard" className={styles.brand}>
          <div className={styles.brandMark}>T</div>
          <div>
            TicketHub
            <small>{isAdmin ? "Área administrativa" : "Área do usuário"}</small>
          </div>
        </Link>

        <nav className={styles.nav} aria-label="Navegação principal">
          <Link
            href="/dashboard"
            className={`${styles.navLink} ${pathname === "/dashboard" ? styles.navLinkActive : ""}`}
          >
            Eventos
          </Link>
          <Link
            href="/dashboard/ingressos"
            className={`${styles.navLink} ${pathname === "/dashboard/ingressos" ? styles.navLinkActive : ""}`}
          >
            Meus ingressos
          </Link>
          {isAdmin && (
            <Link href="/dashboard/checkin" className={styles.navLink}>
              Admin
            </Link>
          )}
        </nav>

        <div className={styles.profile}>
          {loading ? (
            <span className={styles.userName}>Carregando...</span>
          ) : error ? (
            <span className={styles.userName}>{error}</span>
          ) : (
            <>
              <div className={styles.avatar}>{initials}</div>
              <span className={styles.userName}>{user?.nome}</span>
            </>
          )}
          <button type="button" onClick={handleLogout} className={styles.logoutButton}>
            Sair
          </button>
        </div>
      </div>
    </header>
  );
}

export default function AppShell({ children }: { children: React.ReactNode }) {
  const { isAdmin } = useAuthUser();
  const pathname = usePathname();
  const adminRoute = pathname !== "/dashboard/ingressos";

  if (isAdmin && adminRoute) {
    return (
      <div className={styles.adminLayout}>
        <AdminSidebar />
        <main className={styles.adminContent}>{children}</main>
      </div>
    );
  }

  return (
    <div className={styles.layout}>
      <UserTopbar />
      <main className={styles.content}>{children}</main>
    </div>
  );
}
