package br.edu.ifpb.es.daw.dao;

import br.edu.ifpb.es.daw.entities.Usuario;

public interface UsuarioDAO extends DAO<Usuario, Long> {

    Usuario findUsersByName(String nome) throws PersistenciaDawException;

}