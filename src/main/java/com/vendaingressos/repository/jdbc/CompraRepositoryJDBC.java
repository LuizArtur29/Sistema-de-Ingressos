package com.vendaingressos.repository.jdbc;

import com.vendaingressos.model.Compra;
import java.util.List;

public interface CompraRepositoryJDBC {
    void salvar(Compra compra);
    Compra buscarPorId(Long id);
    List<Compra> listarTodos();
    List<Compra> buscarPorUsuario(Long usuarioId);
    void atualizar(Compra compra);
    void deletar(Long id);
}
