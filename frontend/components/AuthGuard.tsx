"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { expireSession, getStoredToken, isTokenValid } from "@/lib/authToken";

export default function AuthGuard({ children }: { children: React.ReactNode }) {
    const router = useRouter();
    const [ready, setReady] = useState(false);

    useEffect(() => {
        const token = getStoredToken();
        if (!token) {
            router.replace("/?session=missing");
            return;
        }

        if (!isTokenValid(token)) {
            expireSession("expired");
            router.replace("/?session=expired");
            return;
        }

        // sessionStorage is only available after mount, so readiness is resolved here.
        // eslint-disable-next-line react-hooks/set-state-in-effect
        setReady(true);
    }, [router]);

    useEffect(() => {
        const handler = (event: Event) => {
            const reason = event instanceof CustomEvent ? event.detail?.reason : "expired";
            router.replace(`/?session=${reason === "missing" ? "missing" : "expired"}`);
        };
        window.addEventListener("session-expired", handler);
        return () => window.removeEventListener("session-expired", handler);
    }, [router]);

    if (!ready) {
        return (
            <div role="status" aria-live="polite" aria-busy="true" style={{ padding: "2rem" }}>
                Validando sessão...
            </div>
        );
    }

    return <>{children}</>;
}
