package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.PersistenciaDawException;
import br.edu.ifpb.es.daw.dao.impl.SessaoEventoDAOImpl;
import br.edu.ifpb.es.daw.entities.SessaoEvento;

import java.time.LocalDateTime;

public class MainSessaoEventoSave {
    public static void main(String[] args) {
        SessaoEventoDAOImpl dao = new SessaoEventoDAOImpl();
        SessaoEvento novaSessao = new SessaoEvento(
                "Show Principal - Noite de Sábado",
                LocalDateTime.of(2025, 12, 21, 20, 0, 0),
                "DISPONÍVEL"
        );

        try {
            dao.save(novaSessao);
            System.out.println("Sessão de Evento salva com sucesso! ID: " + novaSessao.getIdSessao());
        } catch (PersistenciaDawException e) {
            System.err.println("Erro ao salvar a sessão de evento:");
            e.printStackTrace();
        }
    }
}
