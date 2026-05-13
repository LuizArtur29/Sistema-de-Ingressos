"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";

export default function AuthGuard({ children }: { children: React.ReactNode }) {
    const router = useRouter();
    const [ready, setReady] = useState(false);

    useEffect(() => {
        const token = sessionStorage.getItem("token");
        if (!token) {
            router.replace("/");
            return;
        }
        setReady(true);
    }, [router]);

    useEffect(() => {
        const handler = () => {
            router.replace("/");
        };
        window.addEventListener("session-expired", handler);
        return () => window.removeEventListener("session-expired", handler);
    }, [router]);

    if (!ready) return null; // ou loader

    return <>{children}</>;
}