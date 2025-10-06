package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.PersistenciaDawException;
import br.edu.ifpb.es.daw.dao.impl.SessaoEventoDAOImpl;

public class MainSessaoEventoDeleteAll {
    public static void main(String[] args) {
        SessaoEventoDAOImpl dao = new SessaoEventoDAOImpl();
        try {
            dao.deleteAll();
            System.out.println("Todos os registros de SessaoEvento foram removidos com sucesso.");
        } catch (PersistenciaDawException e) {
            System.err.println("Erro ao remover as sessões de evento:");
            e.printStackTrace();
        }
    }
}
