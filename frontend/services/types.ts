export type LoginRequest= {
    email: string;
    senha: string;
};

export type LoginResponse= {
    jwt: string;
}

export type Evento = {
    id?: number;
    nome: string;
    descricao: string;
    dataInicio: string;
    dataFim: string;
    local: string;
    capacidadeTotal: number;
    status: "ATIVO" | "CANCELADO" | "FINALIZADO";
};