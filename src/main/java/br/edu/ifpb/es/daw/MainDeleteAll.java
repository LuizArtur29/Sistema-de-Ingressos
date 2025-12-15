package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.EventoDAO;
import br.edu.ifpb.es.daw.dao.SessaoEventoDAO;
import br.edu.ifpb.es.daw.dao.UsuarioDAO;
import br.edu.ifpb.es.daw.dao.impl.EventoDAOImpl;
import br.edu.ifpb.es.daw.dao.impl.SessaoEventoDAOImpl;
import br.edu.ifpb.es.daw.dao.impl.UsuarioDAOImpl;
import br.edu.ifpb.es.daw.entities.Evento;
import br.edu.ifpb.es.daw.entities.SessaoEvento;
import br.edu.ifpb.es.daw.entities.Usuario;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;

public class MainDeleteAll {

    public static void main(String[] args) throws DawException {
        try(EntityManagerFactory emf = Persistence.createEntityManagerFactory("venda-ingressos-pu")) {
            UsuarioDAO usuarioDAO = new UsuarioDAOImpl(emf);
            EventoDAO eventoDAO = new EventoDAOImpl(emf);
            SessaoEventoDAO sessaoEventoDAO = new SessaoEventoDAOImpl(emf);

            List<Usuario> usuarios = usuarioDAO.getAll();
            for (Usuario usuario : usuarios) {
                usuarioDAO.delete(usuario.getIdUsuario());
            }

            List<Evento> eventos = eventoDAO.getAll();
            for (Evento evento : eventos) {
                usuarioDAO.delete(evento.getId());
            }

            List<SessaoEvento> sessoesEvento = sessaoEventoDAO.getAll();
            for (SessaoEvento sessaoEvento : sessoesEvento) {
                usuarioDAO.delete(sessaoEvento.getIdSessao());
            }

        }
    }
}
