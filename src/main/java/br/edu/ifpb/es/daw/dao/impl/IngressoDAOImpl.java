package br.edu.ifpb.es.daw.dao.impl;

import br.edu.ifpb.es.daw.dao.IngressoDAO;
import br.edu.ifpb.es.daw.entities.Ingresso;
import br.edu.ifpb.es.daw.entities.Transferencia;
import br.edu.ifpb.es.daw.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

public class IngressoDAOImpl extends AbstractDAOImpl<Ingresso, Long> implements IngressoDAO {
    public IngressoDAOImpl() {
        super(Ingresso.class, JPAUtil.getEntityManagerFactory());
    }

    public Ingresso findByIdComTipoIngresso(Long id) {
        EntityManager em = getEntityManager();
        Ingresso ingresso = null;

        String jpql = "SELECT i FROM Ingresso i " +
                "LEFT JOIN FETCH i.tipoIngresso ti " +
                "WHERE i.idIngresso = :id";

        try {
            ingresso = em.createQuery(jpql, Ingresso.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            System.err.println("Ingresso com ID " + id + " não encontrado.");
            return null;
        } finally {
            em.close();
        }

        return ingresso;
    }
}
