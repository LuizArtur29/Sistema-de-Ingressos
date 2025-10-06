package br.edu.ifpb.es.daw.dao.impl;

import br.edu.ifpb.es.daw.dao.TransferenciaDAO;
import br.edu.ifpb.es.daw.entities.Transferencia;
import br.edu.ifpb.es.daw.util.JPAUtil;

public class TransferenciaDAOImpl extends AbstractDAOImpl<Transferencia, Long> implements TransferenciaDAO {
    public TransferenciaDAOImpl() {
        super(Transferencia.class, JPAUtil.getEntityManagerFactory());
    }
}
