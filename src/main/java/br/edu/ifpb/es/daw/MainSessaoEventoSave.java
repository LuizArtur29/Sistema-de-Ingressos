package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.PersistenciaDawException;
import br.edu.ifpb.es.daw.dao.impl.*;
import br.edu.ifpb.es.daw.entities.*;
import br.edu.ifpb.es.daw.entities.enums.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MainSessaoEventoSave {
    public static void main(String[] args) {
        EventoDAOImpl eventoDAO = new EventoDAOImpl();
        SessaoEventoDAOImpl sessaoDAO = new SessaoEventoDAOImpl();

        try {
            Evento evento;
            try {
                evento = eventoDAO.getByID(1L);
                if (evento == null) {
                    AdministradorDAOImpl adminDAO = new AdministradorDAOImpl();
                    Administrador admin = adminDAO.getByID(1L);
                    if (admin == null) {
                        admin = new Administrador("Admin Padrão Sessao", "admin_sessao@sist.com", "senha", "83900000001");
                        adminDAO.save(admin);
                    }
                    evento = new Evento("Evento Padrão Sessão", "Desc",LocalDate.of(2025, 12, 22), LocalDate.of(2025,12,23), "Local Padrão", 100, Status.APROVADO);
                    evento.setAdministrador(admin);
                    eventoDAO.save(evento);
                }
            } catch (PersistenciaDawException findError){
                System.err.println("Erro ao buscar/criar evento pré-requisito: " + findError.getMessage());
                return;
            }
            System.out.println("Usando Evento ID: " + evento.getId());

            SessaoEvento sessao = new SessaoEvento(
                    "Sessão de Teste com Tipos",
                    LocalDateTime.of(2025, 12, 23, 19, 0),
                    Status.PENDENTE
            );

            sessao.setEventoPai(evento);

            TipoIngresso tipo1 = new TipoIngresso("Pista Lote 1", 50.0, 500, 500, 1);
            TipoIngresso tipo2 = new TipoIngresso("VIP Lote 1", 150.0, 100, 100, 1);

            sessao.addTipoIngresso(tipo1);
            sessao.addTipoIngresso(tipo2);

            sessaoDAO.save(sessao);

            System.out.println("Sessão salva com sucesso! ID: " + sessao.getIdSessao());
            System.out.println("  - Evento Pai ID: " + sessao.getEventoPai().getId());
            System.out.println("  - Tipos de Ingresso salvos:");
            for (TipoIngresso t : sessao.getTiposIngresso()) {
                System.out.println("    - Tipo ID: " + t.getIdTipoIngresso() + ", Nome: " + t.getNomeSetor());
            }

        } catch (PersistenciaDawException e) {
            System.err.println("Erro ao salvar a sessão e seus tipos de ingresso:");
            e.printStackTrace();
        }
    }
}