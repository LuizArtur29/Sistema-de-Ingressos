package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.EventoDAO;
import br.edu.ifpb.es.daw.dao.PersistenciaDawException;
import br.edu.ifpb.es.daw.dao.impl.EventoDAOImpl;
import br.edu.ifpb.es.daw.entities.Evento;
import br.edu.ifpb.es.daw.entities.enums.Status;

import java.time.LocalDate;

public class MainEventoSave {
    public static void main(String[] args) {
        EventoDAOImpl dao = new EventoDAOImpl();
        Evento novoEvento = new Evento(
                "Festival de Verão de Campina Grande",
                "Evento com shows de artistas regionais e nacionais.",
                LocalDate.of(2025, 12, 20),
                LocalDate.of(2025, 12, 22),
                "Parque do Povo",
                10000,
                Status.AGENDADO
        );

        try {
            dao.save(novoEvento);
            System.out.println("Evento salvo com sucesso! ID: " + novoEvento.getId());
        } catch (PersistenciaDawException e) {
            System.err.println("Erro ao salvar o evento:");
            e.printStackTrace();
        }
    }
}
