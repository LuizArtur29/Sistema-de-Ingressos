import AuthGuard from "@/components/AuthGuard";
import AppShell from "@/components/AppShell";
import { AuthUserProvider } from "@/hooks/useAuthUser";

export default function DashboardLayout({ children }: { children: React.ReactNode }) {
  return (
      <AuthGuard>
        <AuthUserProvider>
          <AppShell>{children}</AppShell>
        </AuthUserProvider>
      </AuthGuard>
  );
}
