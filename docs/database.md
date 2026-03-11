# 🗄️ Documentação do Banco de Dados

O sistema utiliza o **PostgreSQL** como banco de dados relacional para garantir a consistência das transações de venda e transferência de ingressos.

## 📐 Modelo Entidade-Relacionamento (DER)
O diagrama atualizado pode ser encontrado em: `docs/architecture/diagrama_der.png`.

## 🗂️ Principais Entidades

### 1. Eventos e Sessões
- **Evento:** Entidade pai que armazena informações gerais (nome, capacidade total).
- **SessaoEvento:** Relacionada ao Evento (1:N), define datas e horários específicos.

### 2. Ingressos e Tipos
- **TipoIngresso:** Define as categorias (VIP, Pista) e preços vinculados a uma Sessão.
- **Ingresso:** Representa o bilhete único gerado após a compra, contendo o QR Code/Hash de validação.

### 3. Usuários e Transações
- **Usuario:** Armazena dados cadastrais e credenciais.
- **Compra:** Registro mestre da transação financeira.
- **Transferencia:** Entidade de auditoria que registra a troca de titularidade de um `Ingresso` entre dois `Usuarios`.

## 🛡️ Regras de Integridade
- **Atomicidade:** A venda de um ingresso e a baixa no estoque do `TipoIngresso` ocorrem em uma única transação.
- **Unicidade:** O CPF do usuário é único, e um ingresso não pode ser transferido se já houver uma transferência pendente.

---
**Status do Schema:** ![Stable](https://img.shields.io/badge/Schema-Estável-green)