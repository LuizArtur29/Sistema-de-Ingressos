# Sistema de Ingressos - Frontend

Este é o frontend do projeto Sistema de Ingressos, construído com [Next.js](https://nextjs.org), React e TypeScript.

## Requisitos

- Node.js 20+
- npm

## Instalação

Para garantir que todas as dependências (incluindo as ferramentas de linting) sejam instaladas corretamente e com as versões exatas do `package-lock.json`, use o comando:

```bash
npm ci
```

## Scripts Disponíveis

- `npm run dev`: Inicia o servidor de desenvolvimento.
- `npm run build`: Cria a versão otimizada de produção.
- `npm run start`: Inicia o servidor de produção (requer `npm run build` antes).
- `npm run lint`: Executa a verificação de código utilizando ESLint.
- `npm run verify`: Executa o lint e o build sequencialmente. Útil para validar mudanças antes de realizar commits ou abrir PRs.

## Executando Localmente

Para iniciar o ambiente de desenvolvimento:

```bash
npm run dev
```

Acesse [http://localhost:3000](http://localhost:3000) no seu navegador para ver o resultado.
O frontend utiliza a API local por padrão em `http://localhost:8080`. Se você precisar alterar o endereço da API, pode definir a variável de ambiente:

```bash
NEXT_PUBLIC_API_URL=http://localhost:8080 npm run dev
```

## Comportamento do Dashboard de Eventos

O painel principal (Dashboard) separa os eventos em duas abas principais para evitar confusão entre a listagem global de eventos da plataforma e as ações particulares do usuário autenticado:

1. **Eventos Disponíveis**:
   - Mostra a listagem de eventos com status `ATIVO` na plataforma, ou seja, todos aqueles disponíveis para que novos ingressos sejam comprados.
   - O título e subtítulo são adaptados para uma linguagem neutra e informativa sobre a compra de ingressos.
   
2. **Meus Eventos**:
   - Exibe informações sob demanda a depender do perfil do usuário logado:
     - **Clientes comuns (Usuários)**: Exibe os eventos para os quais o usuário comprou pelo menos um ingresso. Se a lista estiver vazia, exibe uma sugestão com um botão de atalho para explorar e adquirir ingressos na aba "Eventos Disponíveis".
     - **Administradores**: Exibe a lista de eventos criados pelo próprio administrador logado, facilitando a edição e gerenciamento desses festivais. Se vazia, exibe um botão para criar o primeiro evento.
   - Fornece um menu de filtros para refinar a pesquisa por estado do evento (`TODOS`, `ATIVO`, `CANCELADO`, `FINALIZADO`).

As métricas mostradas nos cartões estatísticos superiores atualizam-se dinamicamente para refletir o escopo da aba selecionada no momento.
