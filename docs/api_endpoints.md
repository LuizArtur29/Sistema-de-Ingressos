# 🔌 Documentação da API

Esta API segue os princípios REST e utiliza JSON como formato de intercâmbio de dados para o sistema de gerenciamento de ingressos.

---

## 🚀 Documentação Interativa (Swagger)
Para testar os endpoints em tempo real e visualizar os modelos de dados detalhados, acesse o Swagger UI com o backend em execução:

🔗 **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

---

## 🔐 Autenticação
O sistema utiliza **JWT (JSON Web Token)** para proteger rotas sensíveis.

1.  **Login:** Realize a autenticação em `/api/auth/login`.
2.  **Token:** Capture o token retornado no corpo da resposta.
3.  **Uso:** Envie o token em todas as requisições protegidas através do Header:
    `Authorization: Bearer <seu_token>`

---

## 📍 Endpoints Principais

### 🎫 Eventos e Sessões
| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `GET` | `/api/eventos` | Lista todos os eventos ativos. |
| `GET` | `/api/eventos/{id}` | Retorna detalhes de um evento específico e suas sessões. |
| `POST` | `/api/eventos` | Cadastra um novo evento (Requer privilégios de Admin). |

### 🛒 Compras e Ingressos
| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `POST` | `/api/compras` | Processa a compra de ingressos para um usuário. |
| `GET` | `/api/usuarios/meus-ingressos` | Lista todos os ingressos ativos do usuário autenticado. |

### 🔄 Transferências (P2P)
| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `POST` | `/api/transferencias` | Realiza a transferência de titularidade de um ingresso para outro usuário. |

---

## 📝 Observações Técnicas
- **Filtros:** A listagem de eventos suporta filtros por nome, categoria e data via *Query Parameters*.
- **Validação:** Todos os inputs são validados via Spring Validation. Erros retornam o código `400 Bad Request` com os detalhes do campo.

---
**Status da API:** ![Development](https://img.shields.io/badge/Status-Em%20Desenvolvimento-yellow)