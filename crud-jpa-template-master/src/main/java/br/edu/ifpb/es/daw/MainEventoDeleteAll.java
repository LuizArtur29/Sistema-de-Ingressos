package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.PersistenciaDawException;
import br.edu.ifpb.es.daw.dao.impl.EventoDAOImpl;

public class MainEventoDeleteAll {
    public static void main(String[] args) {
        EventoDAOImpl dao = new EventoDAOImpl();
        try {
            dao.deleteAll();
            System.out.println("Todos os registros de Evento foram removidos com sucesso.");
        } catch (PersistenciaDawException e) {
            System.err.println("Erro ao remover os eventos:");
            e.printStackTrace();
        }
    }
}
