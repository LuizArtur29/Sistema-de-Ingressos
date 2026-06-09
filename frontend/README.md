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
