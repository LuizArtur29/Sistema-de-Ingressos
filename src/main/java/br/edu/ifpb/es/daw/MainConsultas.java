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
import br.edu.ifpb.es.daw.entities.enums.Status;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.time.LocalDateTime;

public class MainConsultas {

    public static void main(String[] args) throws DawException {
        try (EntityManagerFactory emf = Persistence.createEntityManagerFactory("venda-ingressos-pu")) {
            UsuarioDAO usuarioDAO = new UsuarioDAOImpl(emf);
            EventoDAO eventoDAO = new EventoDAOImpl(emf);
            SessaoEventoDAO sessaoEventoDAO = new SessaoEventoDAOImpl(emf);

            // findUsersByName - Usuario
            Usuario usuarioProcurado = usuarioDAO.findUsersByName("José Dantas");
            System.out.println(">>> " + usuarioProcurado.toString());

            // findEventByNameAndLocalEvent - Evento
            Evento eventoProcurado1 = eventoDAO.findEventByNameAndLocalEvent("Workshop de Figma Avançado","Sala de Treinamentos, Bloco C");
            System.out.println(">>> " + eventoProcurado1.toString());

            // getTheCountOfAllEvents - Evento
            System.out.println(">>> " + eventoDAO.getTheCountOfAllEvents());

            // findEventByNameWithAllSsections - Evento
            Evento eventoProcurado2 = eventoDAO.findEventByNameWithAllSsections("Reunião de Alinhamento Q4");

            for (SessaoEvento sessaoEvento : eventoProcurado2.getSessoes()) {
                System.out.println(">>> Evento: Reunião de Alinhamento Q4 - Sessão: " + sessaoEvento.toString());
            }

            // findEventBySectionEventObject - SessaoEvento
            SessaoEvento s2 = new SessaoEvento("Cerimônia de Encerramento e Premiação",LocalDateTime.of(2024, 1, 15, 21, 0), Status.APROVADO);
            Evento eventoProcurado3 = eventoDAO.findEventBySectionEventObject(s2);
            System.out.println(">>> " + eventoProcurado3.toString());

        }

    }
}
