package br.edu.ifpb.es.daw.dao.impl;

import br.edu.ifpb.es.daw.dao.PersistenciaDawException;
import br.edu.ifpb.es.daw.dao.SessaoEventoDAO;
import br.edu.ifpb.es.daw.entities.Evento;
import br.edu.ifpb.es.daw.entities.SessaoEvento;
import br.edu.ifpb.es.daw.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

public class SessaoEventoDAOImpl extends AbstractDAOImpl<SessaoEvento, Long> implements SessaoEventoDAO {
    public SessaoEventoDAOImpl() {
        super(SessaoEvento.class, JPAUtil.getEntityManagerFactory());
    }

    @Override
    public SessaoEvento findSectionEventByEventObject(Evento eventoPai) throws PersistenciaDawException {
        try(EntityManager em = getEntityManager()) {
            SessaoEvento resultado = null;

            TypedQuery<SessaoEvento> query = em.createQuery("SELECT se FROM SessaoEvento se WHERE se.eventoPai = :eventoPai", SessaoEvento.class);
            query.setParameter("eventoPai", eventoPai);
            resultado = query.getSingleResult();
            return resultado;
        } catch (PersistenceException pe) {
            pe.printStackTrace();
            throw new PersistenciaDawException("Ocorreu um erro em achar a Sessão Evento através do objeto Evento", pe);
        }
    }
}
