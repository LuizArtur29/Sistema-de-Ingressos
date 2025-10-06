package br.edu.ifpb.es.daw.dao.impl;

import br.edu.ifpb.es.daw.dao.AdministradorDAO;
import br.edu.ifpb.es.daw.entities.Administrador;
import br.edu.ifpb.es.daw.util.JPAUtil;

public class AdministradorDAOImpl extends AbstractDAOImpl<Administrador, Long> implements AdministradorDAO {
    public AdministradorDAOImpl() {
        super(Administrador.class, JPAUtil.getEntityManagerFactory());
    }
}
