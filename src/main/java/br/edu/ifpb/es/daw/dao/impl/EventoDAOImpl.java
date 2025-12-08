package br.edu.ifpb.es.daw.dao.impl;

import br.edu.ifpb.es.daw.dao.EventoDAO;
import br.edu.ifpb.es.daw.dao.PersistenciaDawException;
import br.edu.ifpb.es.daw.entities.Evento;
import br.edu.ifpb.es.daw.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;

public class EventoDAOImpl extends AbstractDAOImpl<Evento, Long> implements EventoDAO {
    public EventoDAOImpl() {
        super(Evento.class, JPAUtil.getEntityManagerFactory());
    }

    @Override
    public Evento findEventByNameAndLocalEvent(String nomeEvento, String local) throws PersistenciaDawException {
        try(EntityManager em = getEntityManager()) {
            Evento resultado = null;

            TypedQuery<Evento> query = em.createQuery("SELECT e FROM Evento e WHERE e.nome = :nomeEvento AND e.local = :local", Evento.class);
            query.setParameter("nomeEvento", nomeEvento);
            query.setParameter("local", local);
            resultado = query.getSingleResult();
            return resultado;
        } catch (PersistenceException pe) {
            pe.printStackTrace();
            throw new PersistenciaDawException("Ocorreu algum erro ao tentar recuperar o evento a partir do nome e do local do evento.", pe);
        }
    }

    @Override
    public Long getTheCountOfAllEvents() throws PersistenciaDawException {
        try(EntityManager em = getEntityManager()) {
            Long resultado = null;

            TypedQuery<Long> query = em.createQuery("SELECT COUNT(e.nome) FROM Evento e", Long.class);
            resultado = query.getSingleResult();
            return resultado;
        } catch (PersistenceException pe) {
            pe.printStackTrace();
            throw new PersistenciaDawException("Ocorreu algum erro ao tentar recuperar a contagem de todos os eventos.", pe);
        }
    }

    @Override
    public Evento findEventByNameWithAllSsections(String nomeEvento) throws PersistenciaDawException {
        try(EntityManager em = getEntityManager()) {
            Evento resultado = null;

            TypedQuery<Evento> query = em.createQuery("SELECT DISTINCT e FROM Evento e JOIN FETCH e.sessoes WHERE e.nome = :nome", Evento.class);
            query.setParameter("nome", nomeEvento);
            resultado = query.getSingleResult();
            return resultado;
        } catch (PersistenceException pe) {
            pe.printStackTrace();
            throw new PersistenciaDawException("Ocorreu algum erro ao tentar recuperar um evento (com todas as suas sessões) pelo nome.", pe);
        }
    }
}
