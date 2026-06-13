"use client";

import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { clearToken } from "@/lib/authToken";
import styles from "./AppShell.module.css";

const links = [
  { href: "/dashboard", label: "Dashboard", icon: "▦" },
  { href: "/dashboard/create", label: "Novo evento", icon: "+" },
  { href: "/dashboard/vendas", label: "Vendas", icon: "$" },
  { href: "/dashboard/checkin", label: "Check-in", icon: "✓" },
];

export default function AdminSidebar() {
  const pathname = usePathname();
  const router = useRouter();

  const handleLogout = () => {
    clearToken();
    router.push("/");
  };

  return (
    <aside className={styles.sidebar}>
      <Link href="/dashboard" className={styles.brand}>
        <div className={styles.brandMark}>T</div>
        <div>
          TicketHub
          <small>Admin</small>
        </div>
      </Link>

      <div className={styles.sideLabel}>Principal</div>
      <nav className={styles.sideNav}>
        {links.map((link) => (
          <Link
            key={link.href}
            href={link.href}
            className={`${styles.sideLink} ${pathname === link.href ? styles.sideLinkActive : ""}`}
          >
            <span>{link.icon}</span>
            {link.label}
          </Link>
        ))}
      </nav>

      <div className={styles.sideLabel}>Conta</div>
      <nav className={styles.sideNav}>
        <Link href="/dashboard/ingressos" className={styles.sideLink}>
          <span>U</span>
          Ver como usuário
        </Link>
        <button type="button" className={styles.sideButton} onClick={handleLogout}>
          <span>↩</span>
          Sair
        </button>
      </nav>
    </aside>
  );
}
