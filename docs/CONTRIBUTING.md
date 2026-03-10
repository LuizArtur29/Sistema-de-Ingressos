# 🤝 Guia de Contribuição

Este guia define os padrões de desenvolvimento para o **Sistema de Ingressos**. Para manter a organização entre o time (@WolgrandAP, Luiz e Mateus), seguimos o fluxo de trabalho **GitFlow**, **Conventional Commits** e gestão via **Issues**.

---

## 🌿 Estrutura de Branches

1.  **`master`**: Código estável em produção.
2.  **`develop`**: Branch principal de integração.
3.  **`feature/`**: Tarefas específicas (ex: `feature/issue-10-setup-front`).

---

## 🔄 Fluxo de Trabalho Completo (Lifecycle)

Para garantir a qualidade, toda tarefa deve seguir este ciclo de vida:

### 1. Preparação (Issue)
- Nenhuma tarefa deve ser iniciada sem uma **Issue** aberta no GitHub.
- Escolha a Issue no Board, atribua a você mesmo (**Assignee**) e mova para a coluna **"In Progress"**.

### 2. Desenvolvimento Local
- Crie sua branch a partir da `develop` atualizada:
  ```bash
  git checkout develop
  git pull origin develop
  git checkout -b feature/issue-[numero]-descricao-curta

### 3. Commits Semânticos
Utilize o padrão para facilitar o histórico:

**`feat`**: Novas funcionalidades.

**`fix`**: Correções ded bugs.

**`docs`**: Documentação.

**`chore`**: build, infra, dependências.

### 4. Sincronização e Push
- Antes de enviar, garanta que seu código está integrado com o que há de mais novo:
  ```bash
  git pull origin develop
  git push origin feature/issue-[numero]-descricao-curta

### 5. Pull Request (PR) & Code Review
- Abra um PR da sua branch para `develop`.
- Na dedscrição, utilize a palavra-chave: `Close #10` (para fechar a issue automaticamente).
- Solicite a revisão ded pelo menos um colega do time.
- **Ajustes:** Se o revisor pedir mudanças, faça-as na mesma branch e dê push; o PR atualizará sozinho.

### 6. Finalização (Merge & Cleanup)
Após o PR ser aprovado e o Merge realizado pelo autor ou líder técnico:
- **Exclua a branch remota** no GitHub (clique no botão "Delete branch" que aparece após o merge).
- **Limpeza local:**
  ```bash
  git checkout develop
  git pull origin develop
  git branch -d feature/issue-[numero]-descricao-curta

---
## 📝 Padrão de Mensagens
Exemplo:

 `feat: implementa integração com PostGIS para busca de eventos`