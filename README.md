# Sistema de Ingressos

Sistema web para gerenciamento, venda e transferência de ingressos para eventos.

## Estado Atual

Implementado hoje:

- API REST em Java 21 com Spring Boot 3.5.3.
- Autenticação JWT com Spring Security.
- Persistência com Spring Data JPA/Hibernate e PostgreSQL.
- Swagger/OpenAPI exposto pelo backend.
- Frontend em Next.js 16, React 18 e TypeScript.
- Telas de autenticação, dashboard e criação de eventos no frontend.
- Docker Compose para PostgreSQL, Redis, backend e proxy Nginx.

Planejado ou ainda não integrado na aplicação:

- PostGIS/geolocalização. O banco atual é `postgres:15` sem extensão PostGIS e não há campos espaciais no modelo.
- Uso de Redis em cache/sessão. O serviço existe no Compose, mas o backend não possui dependência Spring Data Redis nem código usando Redis.
- Migrations versionadas com Flyway/Liquibase. Em desenvolvimento, o schema é criado/atualizado por `spring.jpa.hibernate.ddl-auto=update`.
- Frontend no Docker Compose. Hoje ele roda localmente via `npm run dev`.

## Arquitetura

O monorepo está organizado assim:

| Caminho | Descrição |
| :--- | :--- |
| `backend/` | API Spring Boot, controllers REST, serviços, repositórios JPA e configuração de segurança |
| `frontend/` | Aplicação Next.js com App Router |
| `docs/` | Documentação técnica, endpoints e diagrama relacional |
| `infra/nginx/` | Proxy Nginx usado pelo Docker Compose |
| `docker-compose.yml` | Serviços locais de banco, Redis, API e Nginx |

Fluxo local recomendado: PostgreSQL no Docker, backend executado pelo Maven Wrapper e frontend executado pelo npm.

## Requisitos

- Docker e Docker Compose.
- JDK 21.
- Node.js 20+ e npm.

## Setup Local End-to-End

### 1. Configurar variáveis

Na raiz do repositório:

```bash
cp .env.example .env
```

Os valores de `.env.example` já funcionam para desenvolvimento local. O backend importa esse arquivo quando é executado a partir de `backend/`.

Variáveis principais:

| Variável | Uso |
| :--- | :--- |
| `DB_LOCAL` | Nome do banco criado pelo container PostgreSQL |
| `DB_LOCAL_USER` | Usuário do PostgreSQL local |
| `DB_LOCAL_PASS` | Senha do PostgreSQL local |
| `JWT_SECRET_BASE64` | Chave Base64 usada para assinar tokens JWT |
| `SECURITY_BOOTSTRAP_ADMIN_ENABLED` | Liga/desliga criação automática de admin local |
| `BOOTSTRAP_ADMIN_*` | Dados do admin criado quando o bootstrap está habilitado |

Para criar um admin automaticamente no primeiro start com banco vazio, altere em `.env`:

```properties
SECURITY_BOOTSTRAP_ADMIN_ENABLED=true
```

### 2. Subir o banco local

```bash
docker compose up -d db
```

O container publica o PostgreSQL em `localhost:5433`, que é a porta usada por `backend/src/main/resources/application-dev.properties`.

Para reiniciar o banco do zero:

```bash
docker compose down -v
docker compose up -d db
```

### 3. Subir o backend

```bash
cd backend
./mvnw spring-boot:run
```

Serviços do backend:

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

O profile ativo padrão é `dev`. Nesse profile, o Hibernate atualiza o schema automaticamente no PostgreSQL local.

### 4. Subir o frontend

Em outro terminal:

```bash
cd frontend
npm install
npm run dev
```

O frontend fica em `http://localhost:3000`.

Por padrão, ele chama a API em `http://localhost:8080`. Para sobrescrever a URL:

```bash
NEXT_PUBLIC_API_URL=http://localhost:8080 npm run dev
```

### 5. Alternativa: backend via Docker Compose

Para subir banco, Redis, backend e Nginx no Docker:

```bash
docker compose up -d --build
```

Endereços nessa opção:

- API direta: `http://localhost:8080`
- API via Nginx: `http://localhost`
- PostgreSQL local: `localhost:5433`

O frontend ainda deve ser iniciado separadamente em `frontend/`.

## Comandos Úteis

Backend:

```bash
cd backend
./mvnw test
./mvnw spring-boot:run
```

Frontend:

```bash
cd frontend
npm run lint
npm run build
```

Docker:

```bash
docker compose ps
docker compose logs -f db
docker compose logs -f api
```

## Documentação

- [Banco de dados e modelo relacional](./docs/database.md)
- [Endpoints da API](./docs/api_endpoints.md)
- [Guia de contribuição](./docs/CONTRIBUTING.md)
- [Diagrama relacional](./docs/architecture/modelo-relacional.png)

## Autores

[Wolgrand (@WolgrandAP)](https://github.com/WolgrandAP), [Luiz Artur (@LuizArtur29)](https://github.com/LuizArtur29) e [Mateus Virginio (@MateusVirginio)](https://github.com/MateusVirginio).
