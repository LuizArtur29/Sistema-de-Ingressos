package br.edu.ifpb.es.daw.dao.impl;

import br.edu.ifpb.es.daw.dao.PersistenciaDawException;
import br.edu.ifpb.es.daw.dao.UsuarioDAO;
import br.edu.ifpb.es.daw.entities.Usuario;
import br.edu.ifpb.es.daw.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;

public class UsuarioDAOImpl extends AbstractDAOImpl<Usuario, Long> implements UsuarioDAO {
    public UsuarioDAOImpl() {
        super(Usuario.class, JPAUtil.getEntityManagerFactory());
    }

    @Override
    public Usuario findUsersByName(String nome) throws PersistenciaDawException {
        try(EntityManager em = getEntityManager()) {
            Usuario resultado = null;

            TypedQuery<Usuario> query = em.createQuery("SELECT u FROM Usuario u WHERE u.nome = :nome", Usuario.class);
            query.setParameter("nome", nome);
            resultado = query.getSingleResult();
            return resultado;
        } catch (PersistenceException pe) {
            pe.printStackTrace();
            throw new PersistenciaDawException("Ocorreu algum erro ao tentar recuperar a pessoa pelo nome.", pe);
        }
    }
}
