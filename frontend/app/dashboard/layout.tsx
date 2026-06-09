import AuthGuard from "@/components/AuthGuard";
import DashboardTopbar from "@/components/DashboardTopbar";
import { AuthUserProvider } from "@/hooks/useAuthUser";
import styles from "./layout.module.css"

export default function DashboardLayout({ children }: { children: React.ReactNode }) {
  return (
      <AuthGuard>
        <AuthUserProvider>
          <div className={styles.layout}>
            <DashboardTopbar />
            <main className={styles.content}>{children}</main>
          </div>
        </AuthUserProvider>
      </AuthGuard>
  );
}
