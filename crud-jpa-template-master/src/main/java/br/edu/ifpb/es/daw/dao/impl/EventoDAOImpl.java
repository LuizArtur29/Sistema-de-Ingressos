package br.edu.ifpb.es.daw.dao.impl;

import br.edu.ifpb.es.daw.dao.EventoDAO;
import br.edu.ifpb.es.daw.entities.Evento;
import br.edu.ifpb.es.daw.util.JPAUtil;

public class EventoDAOImpl extends AbstractDAOImpl<Evento, Long> implements EventoDAO {
    public EventoDAOImpl() {
        super(Evento.class, JPAUtil.getEntityManagerFactory());
    }
}
