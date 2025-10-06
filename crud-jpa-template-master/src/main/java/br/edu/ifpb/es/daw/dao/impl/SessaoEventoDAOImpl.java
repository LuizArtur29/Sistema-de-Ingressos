package br.edu.ifpb.es.daw.dao.impl;

import br.edu.ifpb.es.daw.dao.SessaoEventoDAO;
import br.edu.ifpb.es.daw.entities.SessaoEvento;
import br.edu.ifpb.es.daw.util.JPAUtil;

public class SessaoEventoDAOImpl extends AbstractDAOImpl<SessaoEvento, Long> implements SessaoEventoDAO {
    public SessaoEventoDAOImpl() {
        super(SessaoEvento.class, JPAUtil.getEntityManagerFactory());
    }
}
