package br.edu.ifpb.es.daw.dao.impl;

import br.edu.ifpb.es.daw.dao.PersistenciaDawException;
import br.edu.ifpb.es.daw.dao.SessaoEventoDAO;
import br.edu.ifpb.es.daw.entities.SessaoEvento;
import br.edu.ifpb.es.daw.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

public class SessaoEventoDAOImpl extends AbstractDAOImpl<SessaoEvento, Long> implements SessaoEventoDAO {
    public SessaoEventoDAOImpl() {
        super(SessaoEvento.class, JPAUtil.getEntityManagerFactory());
    }

    public SessaoEventoDAOImpl(EntityManagerFactory emf) {
        super(SessaoEvento.class, emf);
    }

    @Override
    public SessaoEvento findSessaoByName(String nomeSessao) throws PersistenciaDawException {
        try (EntityManager em = getEntityManager()) {
            SessaoEvento sessao = null;

            TypedQuery<SessaoEvento> query = em.createQuery("SELECT s FROM SessaoEvento s WHERE s.nomeSessao = :nomeSessao", SessaoEvento.class);
            query.setParameter("nomeSessao", nomeSessao);
            sessao = query.getSingleResult();
            return sessao;
        } catch (PersistenceException pe) {
            pe.printStackTrace();
            throw new PersistenciaDawException("Ocorreu algum erro ao tentar recuperar a sessão pelo seu nome.", pe);
        }
    }
}
