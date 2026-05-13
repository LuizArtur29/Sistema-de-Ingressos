"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import styles from "@/app/dashboard/layout.module.css";

export default function DashboardTopbar() {
    const router = useRouter();

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
                <div className={styles.avatar}>AS</div>
                <span className={styles.userName}>Ana Souza</span>
                <button onClick={handleLogout} className={styles.logoutButton}>
                    Sair
                </button>
            </div>
        </header>
    );
}