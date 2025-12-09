package com.vendaingressos.repository.jdbc;

import com.vendaingressos.model.SessaoEvento;

import java.util.List;

public interface SessaoEventoRepository {
    void salvar(SessaoEvento sessaoEvento);
    SessaoEvento buscarPorId(Long id);
    List<SessaoEvento> listarTodos();
    List<SessaoEvento> buscarPorEventoPai(Long eventoPaiId);
    void atualizar(SessaoEvento sessaoEvento);
    void deletar(Long id);
}
