# Documentacao da API

Esta API segue os principios REST e utiliza JSON como formato de intercambio de dados para o sistema de gerenciamento de ingressos.

Todas as rotas de negocio seguem o prefixo comum `/api`.

---

## Documentacao Interativa (Swagger)

Para testar os endpoints em tempo real e visualizar os modelos de dados detalhados, acesse o Swagger UI com o backend em execucao:

`http://localhost:8080/swagger-ui.html`

A especificacao OpenAPI fica disponivel em:

`http://localhost:8080/v3/api-docs`

---

## Autenticacao

O sistema utiliza JWT para proteger rotas sensiveis.

1. Realize a autenticacao em `POST /api/auth/login`.
2. Capture o token retornado no corpo da resposta.
3. Envie o token nas rotas protegidas pelo header `Authorization: Bearer <seu_token>`.

---

## Endpoints

### Autenticacao

| Metodo | Endpoint | Acesso | Descricao |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/login` | Publico | Autentica usuario e retorna token JWT. |

### Usuarios

| Metodo | Endpoint | Acesso | Descricao |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/usuarios` | Publico | Cadastra um novo usuario. |
| `GET` | `/api/usuarios` | Admin | Lista todos os usuarios. |
| `GET` | `/api/usuarios/{id}` | Admin | Busca usuario por ID. |
| `GET` | `/api/usuarios/email/{email}` | Admin | Busca usuario por e-mail. |
| `PUT` | `/api/usuarios/{id}` | Admin | Atualiza usuario por ID. |
| `DELETE` | `/api/usuarios/{id}` | Admin | Remove usuario por ID. |
| `GET` | `/api/usuarios/me` | Usuario/Admin | Retorna o perfil do usuario autenticado. |
| `PUT` | `/api/usuarios/me` | Usuario/Admin | Atualiza o perfil do usuario autenticado. |

### Eventos

| Metodo | Endpoint | Acesso | Descricao |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/eventos` | Publico | Lista todos os eventos. |
| `GET` | `/api/eventos/{id}` | Publico | Retorna detalhes de um evento especifico. |
| `POST` | `/api/eventos` | Admin | Cadastra um novo evento. |
| `PUT` | `/api/eventos/{id}` | Admin | Atualiza um evento. |
| `DELETE` | `/api/eventos/{id}` | Admin | Remove um evento. |

### Sessoes de Evento

| Metodo | Endpoint | Acesso | Descricao |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/sessoes-evento` | Admin | Cadastra uma sessao de evento. |
| `GET` | `/api/sessoes-evento` | Admin | Lista todas as sessoes de evento. |
| `GET` | `/api/sessoes-evento/{id}` | Admin | Busca sessao de evento por ID. |
| `GET` | `/api/sessoes-evento/evento/{eventoId}` | Admin | Lista sessoes vinculadas a um evento. |
| `PUT` | `/api/sessoes-evento/{id}` | Admin | Atualiza uma sessao de evento. |
| `DELETE` | `/api/sessoes-evento/{id}` | Admin | Remove uma sessao de evento. |

### Tipos de Ingresso

| Metodo | Endpoint | Acesso | Descricao |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/tipos-ingresso` | Admin | Cadastra um tipo de ingresso para uma sessao. |
| `GET` | `/api/tipos-ingresso/sessao/{sessaoId}` | Admin | Lista tipos de ingresso de uma sessao. |

### Ingressos

| Metodo | Endpoint | Acesso | Descricao |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/ingressos/sessoes/{sessaoEventoId}/tipos/{tipoIngressoId}` | Admin | Cria ingresso para uma sessao e tipo de ingresso. |
| `GET` | `/api/ingressos/sessoes/{sessaoEventoId}` | Admin | Lista ingressos de uma sessao. |
| `GET` | `/api/ingressos/{ingressoId}` | Admin | Busca ingresso por ID. |
| `GET` | `/api/ingressos/{ingressoId}/disponibilidade` | Admin | Verifica se o ingresso esta disponivel. |
| `PUT` | `/api/ingressos/{ingressoId}/entrada` | Admin | Registra entrada de um ingresso. |

### Compras

| Metodo | Endpoint | Acesso | Descricao |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/compras` | Usuario/Admin | Processa uma compra de ingresso. |
| `GET` | `/api/compras` | Usuario/Admin | Lista todas as compras. |
| `GET` | `/api/compras/{id}` | Usuario/Admin | Busca compra por ID. |
| `GET` | `/api/compras/usuario/{usuarioId}` | Usuario/Admin | Lista compras de um usuario. |
| `PUT` | `/api/compras/{id}/status` | Usuario/Admin | Atualiza status de uma compra. |
| `DELETE` | `/api/compras/{id}` | Usuario/Admin | Remove uma compra. |

### Transferencias

| Metodo | Endpoint | Acesso | Descricao |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/transferencias` | Usuario/Admin | Realiza a transferencia de titularidade de um ingresso. |

Payload:

```json
{
  "ingressoId": 10,
  "compradorId": 2,
  "valorRevenda": 50.0
}
```

Resposta:

```json
{
  "idTransferencia": 1,
  "ingressoId": 10,
  "vendedorId": 1,
  "compradorId": 2,
  "valorRevenda": 50.0
}
```

### Administradores

| Metodo | Endpoint | Acesso | Descricao |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/administradores` | Admin | Cadastra administrador. |
| `GET` | `/api/administradores` | Admin | Lista administradores. |

---

## Erros Comuns

| Status | Quando ocorre |
| :--- | :--- |
| `400 Bad Request` | Payload invalido, campos obrigatorios ausentes ou regra de negocio violada. |
| `401 Unauthorized` | Token ausente, invalido ou expirado. |
| `403 Forbidden` | Usuario autenticado sem permissao para a rota. |
| `404 Not Found` | Recurso nao encontrado. |

---

## Observacoes Tecnicas

- Nao ha endpoints legados fora de `/api` para recursos de negocio.
- Rotas antigas de ingressos em `/ingressos/**` foram substituidas pelas rotas REST em `/api/ingressos/**`.
- A documentacao interativa do Swagger deve refletir automaticamente os controllers atuais via Springdoc.

---

**Status da API:** Em desenvolvimento.
