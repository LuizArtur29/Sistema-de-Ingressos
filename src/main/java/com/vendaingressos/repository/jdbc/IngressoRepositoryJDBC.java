package com.vendaingressos.repository.jdbc;

import com.vendaingressos.model.Ingresso;

import java.util.List;

public interface IngressoRepositoryJDBC {
    void salvar(Ingresso ingresso);
    Ingresso buscarPorId(Long id);
    List<Ingresso> listarTodos();
    Long contarPorSessaoEvento(Long sessaoEventoId);
    void atualizar(Ingresso ingresso);
    void deletar(Long id);
}
