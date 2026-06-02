# Guia de Contribuição

Este guia define os padrões de desenvolvimento do Sistema de Ingressos. O fluxo atual usa GitFlow, Conventional Commits e gestão por Issues.

## Branches

| Branch | Uso |
| :--- | :--- |
| `master` | Código estável |
| `develop` | Integração das entregas |
| `feature/issue-[numero]-descricao-curta` | Desenvolvimento de uma issue específica |

## Fluxo de Trabalho

### 1. Preparar a issue

- Nenhuma tarefa deve ser iniciada sem uma issue aberta.
- Escolha a issue no board, atribua a você mesmo e mova para `In Progress`.

### 2. Criar a branch

```bash
git checkout develop
git pull origin develop
git checkout -b feature/issue-[numero]-descricao-curta
```

### 3. Commits

Use Conventional Commits:

| Tipo | Uso |
| :--- | :--- |
| `feat` | Nova funcionalidade |
| `fix` | Correção de bug |
| `docs` | Documentação |
| `test` | Testes |
| `chore` | Build, infraestrutura ou dependências |

Exemplos:

```text
feat: adiciona criacao de eventos no dashboard
fix: corrige validacao de cpf no cadastro de usuario
docs: atualiza setup local do projeto
```

### 4. Sincronizar e enviar

```bash
git pull origin develop
git push origin feature/issue-[numero]-descricao-curta
```

### 5. Abrir pull request

- Abra o PR da sua branch para `develop`.
- Na descrição, use a palavra-chave da issue, por exemplo `Close #10`.
- Solicite revisão de pelo menos uma pessoa do time.
- Se houver ajustes, faça novos commits na mesma branch e envie novamente.

### 6. Finalizar

Depois do merge:

```bash
git checkout develop
git pull origin develop
git branch -d feature/issue-[numero]-descricao-curta
```

Se a branch remota não for removida automaticamente pelo GitHub, remova-a manualmente após o merge.
