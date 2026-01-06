--liquibase formatted sql

--changeset luizartur:1
--comment: Ativa a extensão PostGIS para suportar tipos espaciais (Point, Geometry)
CREATE EXTENSION IF NOT EXISTS postgis;

--changeset luizartur:2
--comment: Tabela de usuarios
CREATE TABLE usuario (
                         id_usuario SERIAL PRIMARY KEY,
                         nome VARCHAR(255) NOT NULL,
                         cpf VARCHAR(11) NOT NULL,
                         data_nascimento DATE,
                         email VARCHAR(255) NOT NULL UNIQUE,
                         senha VARCHAR(255) NOT NULL,
                         endereco VARCHAR(255),
                         telefone VARCHAR(20)
);

--changeset luizartur:3
--comment: Tabela de eventos (COM coluna espacial)
CREATE TABLE eventos (
                         id SERIAL PRIMARY KEY,
                         nome VARCHAR(255) NOT NULL,
                         descricao TEXT,
                         data_inicio DATE NOT NULL,
                         data_fim DATE NOT NULL,
                         local VARCHAR(255) NOT NULL,
                         capacidade_total INTEGER NOT NULL,
                         status VARCHAR(50) NOT NULL,
                         imagem_nome VARCHAR(255),
                         localizacao GEOMETRY,  -- Coluna adicionada para o Point do PostGIS
                         admin_id BIGINT,
                         CONSTRAINT fk_evento_admin FOREIGN KEY (admin_id) REFERENCES usuario(id_usuario)
);

--changeset luizartur:4
--comment: Tabela de sessoes_evento
CREATE TABLE sessoes_evento (
                                id_sessao SERIAL PRIMARY KEY,
                                nome_sessao VARCHAR(255) NOT NULL,
                                data_hora_sessao TIMESTAMP NOT NULL,
                                status_sessao VARCHAR(50) NOT NULL,
                                id_evento BIGINT NOT NULL,
                                CONSTRAINT fk_sessao_evento_pai FOREIGN KEY (id_evento) REFERENCES eventos(id) ON DELETE CASCADE
);

--changeset luizartur:5
--comment: Tabela de ingresso
CREATE TABLE ingresso (
                          id_ingresso SERIAL PRIMARY KEY,
                          preco DOUBLE PRECISION NOT NULL,
                          tipo_ingresso VARCHAR(50) NOT NULL,
                          ingresso_disponivel BOOLEAN DEFAULT TRUE,
                          id_sessao_evento BIGINT NOT NULL,
                          CONSTRAINT fk_ingresso_sessao FOREIGN KEY (id_sessao_evento) REFERENCES sessoes_evento(id_sessao)
);

--changeset luizartur:6
--comment: Tabela de compra
CREATE TABLE compra (
                        id_compra SERIAL PRIMARY KEY,
                        data_compra DATE NOT NULL,
                        quantidade_ingressos INTEGER NOT NULL,
                        valor_total DOUBLE PRECISION NOT NULL,
                        metodo_pagamento VARCHAR(50) NOT NULL,
                        status VARCHAR(50) NOT NULL,
                        id_usuario BIGINT NOT NULL,
                        id_ingresso BIGINT NOT NULL,
                        CONSTRAINT fk_compra_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
                        CONSTRAINT fk_compra_ingresso FOREIGN KEY (id_ingresso) REFERENCES ingresso(id_ingresso)
);

--changeset luizartur:7
--comment: Procedure para calcular receita
CREATE OR REPLACE FUNCTION calcular_receita_evento(evento_id BIGINT)
RETURNS DOUBLE PRECISION AS $$
BEGIN
RETURN (
    SELECT COALESCE(SUM(c.valor_total), 0)
    FROM compra c
             INNER JOIN ingresso i ON c.id_ingresso = i.id_ingresso
             INNER JOIN sessoes_evento se ON i.id_sessao_evento = se.id_sessao
    WHERE se.id_evento = evento_id
);
END;
$$ LANGUAGE plpgsql;