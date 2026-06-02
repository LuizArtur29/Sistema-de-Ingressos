# Banco de Dados

O sistema usa PostgreSQL como banco relacional principal. Em desenvolvimento local, o banco roda via Docker Compose e é acessado pelo backend em `localhost:5433`.

## Setup Local

Na raiz do projeto:

```bash
cp .env.example .env
docker compose up -d db
```

Depois, suba o backend a partir de `backend/`:

```bash
cd backend
./mvnw spring-boot:run
```

Com o profile `dev`, o Hibernate usa `spring.jpa.hibernate.ddl-auto=update`, então as tabelas são criadas/atualizadas automaticamente a partir das entidades JPA. O projeto ainda não possui migrations Flyway/Liquibase.

## Configuração Local

Valores esperados pelo profile `dev`:

| Item | Valor |
| :--- | :--- |
| Banco | PostgreSQL 15 |
| Host | `localhost` |
| Porta | `5433` |
| Database | `DB_LOCAL` em `.env` |
| Usuário | `DB_LOCAL_USER` em `.env` |
| Senha | `DB_LOCAL_PASS` em `.env` |

No Docker Compose, a porta externa `5433` aponta para a porta interna `5432` do PostgreSQL.

## Modelo Relacional

O diagrama atual está em [docs/architecture/modelo-relacional.png](./architecture/modelo-relacional.png).

Principais entidades persistidas hoje:

| Entidade | Tabela | Papel |
| :--- | :--- | :--- |
| `Usuario` | `usuario` | Usuários finais, credenciais, dados pessoais e role `USUARIO` |
| `Administrador` | `administrador` | Administradores com role `ADMINISTRADOR` |
| `Evento` | `eventos` | Dados gerais do evento, local, datas, capacidade e status |
| `SessaoEvento` | `sessoes_evento` | Sessões/dias vinculados a um evento |
| `TipoIngresso` | `tipo_ingresso` | Setor/lote/preço e disponibilidade por sessão |
| `Ingresso` | `ingresso` | Bilhete individual associado a sessão, tipo e compra |
| `Compra` | `compra` | Registro de compra, pagamento, status e ingressos |
| `Transferencia` | `transferencia` | Registro de revenda/troca de titularidade entre usuários |

## Relacionamentos Principais

- `Administrador` 1:N `Evento`.
- `Evento` 1:N `SessaoEvento`.
- `SessaoEvento` 1:N `TipoIngresso`.
- `SessaoEvento` 1:N `Ingresso`.
- `TipoIngresso` 1:N `Ingresso`.
- `Usuario` 1:N `Compra`.
- `Compra` 1:N `Ingresso`.
- `Transferencia` referencia vendedor, comprador e ingresso transferido.

## Regras Implementadas

- CPF de usuário deve ter 11 dígitos numéricos.
- Email de usuário deve ter formato válido.
- Eventos e sessões exigem datas futuras ou atuais.
- Compra e transferência são protegidas por autenticação JWT.
- Rotas administrativas exigem role de administrador.

## Itens Planejados

- PostGIS/geolocalização ainda não está integrado ao modelo. Não há campos espaciais nas entidades atuais.
- Migrations versionadas ainda não existem; o schema local depende do Hibernate `ddl-auto=update`.
- Redis não participa da persistência nem do cache da aplicação hoje.

## Reset do Banco Local

Para apagar os dados locais e recriar o volume:

```bash
docker compose down -v
docker compose up -d db
```
