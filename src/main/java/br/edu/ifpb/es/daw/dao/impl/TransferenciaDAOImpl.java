package br.edu.ifpb.es.daw.dao.impl;

import br.edu.ifpb.es.daw.dao.TransferenciaDAO;
import br.edu.ifpb.es.daw.entities.Transferencia;
import br.edu.ifpb.es.daw.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

public class TransferenciaDAOImpl extends AbstractDAOImpl<Transferencia, Long> implements TransferenciaDAO {
    public TransferenciaDAOImpl() {
        super(Transferencia.class, JPAUtil.getEntityManagerFactory());
    }
}
