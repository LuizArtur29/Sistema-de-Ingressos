package br.edu.ifpb.es.daw.dao.impl;

import br.edu.ifpb.es.daw.dao.UsuarioDAO;
import br.edu.ifpb.es.daw.entities.Usuario;
import br.edu.ifpb.es.daw.util.JPAUtil;

public class UsuarioDAOImpl extends AbstractDAOImpl<Usuario, Long> implements UsuarioDAO {
    public UsuarioDAOImpl() {
        super(Usuario.class, JPAUtil.getEntityManagerFactory());
    }
}
