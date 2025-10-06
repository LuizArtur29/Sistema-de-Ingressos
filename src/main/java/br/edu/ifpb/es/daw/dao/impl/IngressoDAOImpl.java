package br.edu.ifpb.es.daw.dao.impl;

import br.edu.ifpb.es.daw.dao.IngressoDAO;
import br.edu.ifpb.es.daw.entities.Ingresso;
import br.edu.ifpb.es.daw.util.JPAUtil;

public class IngressoDAOImpl extends AbstractDAOImpl<Ingresso, Long> implements IngressoDAO {
    public IngressoDAOImpl() {
        super(Ingresso.class, JPAUtil.getEntityManagerFactory());
    }
}
