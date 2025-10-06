package br.edu.ifpb.es.daw.dao.impl;

import br.edu.ifpb.es.daw.dao.CompraDAO;
import br.edu.ifpb.es.daw.entities.Compra;
import br.edu.ifpb.es.daw.util.JPAUtil;

public class CompraDAOImpl extends AbstractDAOImpl<Compra, Long> implements CompraDAO {
    public CompraDAOImpl() {
        super(Compra.class, JPAUtil.getEntityManagerFactory());
    }
}
