export type LoginRequest= {
    email: string;
    senha: string;
};

export type LoginResponse= {
    jwt: string;
}

export type Evento = {
    id: number;
    nome: string;
    descricao: string;
    dataInicio: string;
    dataFim: string;
    local: string;
    capacidadeTotal: number;
    status: "ATIVO" | "CANCELADO" | "FINALIZADO";
};

export type EventoPayload = Omit<Evento, "id">;

export type RegisterRequest = {
    nome: string;
    CPF: string; // precisa ser "CPF"
    dataNascimento: string; // yyyy-mm-dd
    email: string;
    senha: string;
    endereco: string;
    telefone: string;
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

export type RegisterResponse = {
    idUsuario?: number;
    nome: string;
    CPF?: string;
    dataNascimento: string;
    email: string;
    endereco: string;
    telefone: string;
};

export type UsuarioPerfil = {
    idUsuario?: number;
    nome: string;
    cpf?: string;
    dataNascimento?: string;
    email: string;
    endereco?: string;
    telefone?: string;
};

export type SessaoEvento = {
    idSessao: number;
    nomeSessao: string;
    dataHoraSessao: string;
    statusSessao: string;
    capacidade: number | null;
};

export type SessaoEventoPayload = {
    nomeSessao: string;
    dataHoraSessao: string;
    statusSessao: string;
    capacidade: number | null;
    eventoPai: { id: number };
};

export type TipoIngresso = {
    idTipoIngresso: number;
    nomeSetor: string;
    preco: number;
    quantidadeTotal: number;
    quantidadeDisponivel: number;
    lote: number;
};

export type TipoIngressoPayload = {
    nomeSetor: string;
    preco: number;
    quantidadeTotal: number;
    lote: number;
    sessaoId: number;
};