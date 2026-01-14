-- liquibase formatted sql

-- changeset luiz:criar-funcao-receita
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