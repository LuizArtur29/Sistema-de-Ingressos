# 🎫 Sistema de Ingressos

<p align="center">
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java" />
  <img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/Next.js-000000?style=for-the-badge&logo=nextdotjs&logoColor=white" alt="Next.js" />
  <img src="https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL" />
  <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker" />
</p>

Plataforma robusta para gerenciamento, venda e transferência de ingressos para eventos, desenvolvida com foco em escalabilidade e geolocalização.

---

## 📖 Sobre o Projeto

O sistema permite o rastreamento completo do ciclo de vida de um ingresso, desde a criação do evento pelo administrador até a revenda entre usuários finais.

### 🚀 Principais Funcionalidades
* **Gestão de Eventos:** Cadastro de eventos com múltiplas sessões e tipos de ingressos (VIP, Pista, etc.).
* **Venda Granular:** Controle de lotes, preços e disponibilidade em tempo real.
* **Transferência P2P:** Funcionalidade de revenda onde um usuário pode transferir um ingresso para outro com registro de valor negociado.
* **Geolocalização:** Integração com **PostGIS** para busca e exibição de eventos baseada na localização do usuário.

---

## 🏗️ Estrutura do Monorepo

Para facilitar a manutenção e o deploy, o projeto está organizado da seguinte forma:

| Diretório | Descrição |
| :--- | :--- |
| **`/backend`** | API desenvolvida em Java com Spring Boot |
| **`/frontend`** | Interface do usuário em Next.js e Tailwind CSS |
| **`/docs`** | Documentação técnica, diagramas e guias |

---

## 🛠️ Tecnologias Utilizadas

- **Backend:** Java 17+, Spring Boot 3, Spring Security (JWT), Hibernate/JPA.
- **Frontend:** React, Next.js (App Router), TypeScript, Tailwind CSS.
- **Banco de Dados:** PostgreSQL com extensão **PostGIS** para dados espaciais.
- **Cache/Performance:** Redis para gerenciamento de sessões e cache.

---

## ⚙️ Como Executar

O projeto utiliza Docker para orquestrar o ambiente de desenvolvimento.

1. **Clone o repositório:**
   ```bash
   git clone [https://github.com/WolgrandAP/sistema-de-ingressos.git](https://github.com/WolgrandAP/sistema-de-ingressos.git)
   cd sistema-de-ingressos

2. **Suba os serviços (Banco de Dados e Cache):**
   ```bash
   docker-compose up -d

2. **Inicie o Backend:**
   ```bash
    cd backend
    ./mvnw spring-boot:run

---

## 📄 Documentação Adicional

Para detalhes técnicos e guias de processo, acesse os documentos abaixo na pasta `/docs`:

* 🏛️ **[Arquitetura e Banco de Dados](./docs/database.md)**: Detalhes sobre o schema e regras de integridade.
* 🔌 **[Endpoints da API](./docs/api_endpoints.md)**: Guia de integração e rotas principais.
* 🤝 **[Guia de Contribuição](./docs/CONTRIBUTING.md)**: Padrões de GitFlow, Issues e Commits do time.
* 📐 **[Diagrama DER](./docs/architecture/diagrama_der.png)**: Visualização gráfica do banco de dados.
   

---
**Desenvolvido por:** [Wolgrand (@WolgrandAP)](https://github.com/WolgrandAP), [Luiz Artur (@LuizArtur29)](https://github.com/LuizArtur29) e [Mateus Virginio(@MateusVirginio)](https://github.com/MateusVirginio).