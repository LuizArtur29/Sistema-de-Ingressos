package com.vendaingressos.repository.jdbc;

import com.vendaingressos.model.Ingresso;

import java.util.List;

public interface IngressoRepository {
    void salvar(Ingresso ingresso);
    Ingresso buscarPorId(Long id);
    List<Ingresso> listarTodos();
    void atualizar(Ingresso ingresso);
    void deletar(Long id);

    List<Ingresso> buscarPorSessao(Long sessaoId);
    int contarIngressosPorSessao(Long sessaoEventoId);
}
