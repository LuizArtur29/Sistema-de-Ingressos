package br.edu.ifpb.es.daw.dao.impl;


import br.edu.ifpb.es.daw.dao.TipoIngressoDAO;
import br.edu.ifpb.es.daw.entities.TipoIngresso;
import br.edu.ifpb.es.daw.util.JPAUtil;

public class TipoIngressoDAOImpl extends AbstractDAOImpl<TipoIngresso, Long> implements TipoIngressoDAO {
    public TipoIngressoDAOImpl() {
        super(TipoIngresso.class, JPAUtil.getEntityManagerFactory());
    }
}
