package com.vendaingressos.repository.jdbc;

import com.vendaingressos.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepositoryJDBC {
    void salvar(Usuario usuario);
    Optional<Usuario> buscarPorEmail(String email);
    Usuario buscarPorId(Long id);
    List<Usuario> listarTodos();
    void atualizar(Usuario usuario);
    void deletar(Long id);
}
