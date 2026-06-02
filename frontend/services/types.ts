export type LoginRequest = {
    email: string;
    senha: string;
};

export type LoginResponse = {
    jwt: string;
};

export type FieldErrorItem = {
    field: string;
    message: string;
};

export type ApiProblemDetail = {
    title?: string;
    detail?: string;
    errors?: FieldErrorItem[];
};

// ============ EVENTO ============

export type EventoStatus = "ATIVO" | "CANCELADO" | "FINALIZADO";

export type EventoCreateRequest = {
    nome: string;
    descricao: string;
    dataInicio: string;
    dataFim: string;
    local: string;
    capacidadeTotal: number;
    status: EventoStatus;
};

export type SessaoEventoResponse = {
    idSessao: number;
    nomeSessao: string;
    dataHoraSessao: string;
    statusSessao: string;
    capacidade: number | null;
};

export type EventoResponse = {
    id: number;
    nome: string;
    descricao: string;
    dataInicio: string;
    dataFim: string;
    local: string;
    capacidadeTotal: number;
    status: EventoStatus;
    sessoes: SessaoEventoResponse[];
};

// ============ USUÁRIO ============

export type RegisterRequest = {
    nome: string;
    CPF: string;
    dataNascimento: string;
    email: string;
    senha: string;
    endereco: string;
    telefone: string;
};

export type RegisterResponse = {
    idUsuario?: number;
    nome: string;
    CPF?: string;
    dataNascimento: string;
    email: string;
    endereco: string;
    telefone: string;
};

export type UsuarioAutenticado = {
    idUsuario: number;
    nome: string;
    cpf: string;
    dataNascimento: string;
    email: string;
    endereco: string;
    telefone: string;
};

// Se quiser manter o nome usado atualmente na tela:
export type UsuarioPerfil = UsuarioAutenticado;

// ============ SESSÃO ============

export type SessaoEventoRequest = {
    nomeSessao: string;
    dataHoraSessao: string;
    statusSessao: string;
    capacidade: number | null;
    eventoPai: { id: number };
};

// ============ TIPO DE INGRESSO ============

export type TipoIngressoResponse = {
    idTipoIngresso: number;
    nomeSetor: string;
    preco: number;
    quantidadeTotal: number;
    quantidadeDisponivel: number;
    lote: number;
};

export type TipoIngressoCreateRequest = {
    nomeSetor: string;
    preco: number;
    quantidadeTotal: number;
    lote: number;
    sessaoId: number;
};

// ============ COMPRA ============

export type CompraResponse = {
    idCompra: number;
    dataCompra: string;
    quantidadeIngressos: number;
    valorTotal: number;
    metodoPagamento: string;
    status: string;
    usuarioId: number;
    nomeUsuario: string;
    ingressoId?: number;
    nomeEvento?: string;
};

// ============ INGRESSO ============

export type IngressoResponse = {
    idIngresso: number;
    preco: number;
    ingressoDisponivel: boolean;
    vendido: boolean;
    disponivelParaCompra: boolean;
    sessaoEventoId?: number;
    idTipoIngresso?: number;
    nomeTipoIngresso?: string;
};