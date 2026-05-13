import type { Metadata } from "next";
import { ToastProvider } from "@/components/ToastProvider";
import { Inter } from "next/font/google";
import "./globals.css";

const inter = Inter({
  variable: "--font-inter",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "TicketHub - Plataforma de venda de ingressos",
  description: "Gerencie e acompanhe todos os seus eventos.",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
      <html lang="pt-BR">
      <body>
      <ToastProvider>{children}</ToastProvider>
      </body>
      </html>
  );
}