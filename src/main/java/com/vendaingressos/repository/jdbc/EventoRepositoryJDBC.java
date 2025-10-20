package com.vendaingressos.repository.jdbc;

import com.vendaingressos.model.Evento;

import java.util.List;

public interface EventoRepositoryJDBC {
    void salvar(Evento evento);
    Evento buscarPorId(Long id);
    List<Evento> listarTodos();
    void atualizar(Evento evento);
    void deletar(Long id);
}
