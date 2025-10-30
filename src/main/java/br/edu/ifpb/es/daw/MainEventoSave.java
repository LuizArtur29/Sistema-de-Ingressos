package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.PersistenciaDawException;
import br.edu.ifpb.es.daw.dao.impl.AdministradorDAOImpl;
import br.edu.ifpb.es.daw.dao.impl.EventoDAOImpl;
import br.edu.ifpb.es.daw.entities.Administrador;
import br.edu.ifpb.es.daw.entities.Evento;
import br.edu.ifpb.es.daw.entities.SessaoEvento;
import br.edu.ifpb.es.daw.entities.enums.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MainEventoSave {
    public static void main(String[] args) {
        AdministradorDAOImpl adminDAO = new AdministradorDAOImpl();
        EventoDAOImpl eventoDAO = new EventoDAOImpl();

        try {
            Administrador admin;
            try {
                admin = adminDAO.getByID(1L);
                if (admin == null) {
                    admin = new Administrador("Admin Padrão", "admin_padrao@sistema.com", "senha", "83900000000");
                    adminDAO.save(admin);
                }
            } catch (PersistenciaDawException e) {
                admin = new Administrador("Admin Padrão", "admin_padrao@sistema.com", "senha", "83900000000");
                adminDAO.save(admin);
            }
            System.out.println("Usando Admin ID: " + admin.getIdAdmin());

            Evento novoEvento = new Evento(
                    "Festival de Verão de Campina Grande",
                    "Evento com shows de artistas regionais e nacionais.",
                    LocalDate.of(2025, 12, 20),
                    LocalDate.of(2025, 12, 22),
                    "Parque do Povo",
                    10000,
                    Status.PENDENTE
            );

            novoEvento.setAdministrador(admin);
            SessaoEvento sessaoEvento1 = new SessaoEvento("Noite de Abertura",
                    LocalDateTime.of(2025, 12, 22, 19, 0),
                    Status.AGENDADO
            );
            SessaoEvento sessaoEvento2 = new SessaoEvento("Tarde de Encerramento",
                    LocalDateTime.of(2025, 12, 23, 15, 0),
                    Status.AGENDADO);

            novoEvento.addSessao(sessaoEvento1);
            novoEvento.addSessao(sessaoEvento2);
            eventoDAO.save(novoEvento);

            System.out.println("Evento salvo com sucesso! ID: " + novoEvento.getId());
            System.out.println("  - Criador (Admin ID): " + novoEvento.getAdministrador().getIdAdmin());
            System.out.println("  - Sessões salvas:");
            for (SessaoEvento s : novoEvento.getSessoes()) {
                System.out.println("    - Sessao ID: " + s.getIdSessao() + ", Nome: " + s.getNomeSessao());
            }
        } catch (PersistenciaDawException e) {
            System.out.println("Erro ao salvar: " + e.getMessage());
            e.printStackTrace(System.out);
        }
    }
}