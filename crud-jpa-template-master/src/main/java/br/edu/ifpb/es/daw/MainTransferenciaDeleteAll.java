package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.PersistenciaDawException;
import br.edu.ifpb.es.daw.dao.impl.TransferenciaDAOImpl;

public class MainTransferenciaDeleteAll {
    public static void main(String[] args) {
        TransferenciaDAOImpl dao = new TransferenciaDAOImpl();
        try {
            dao.deleteAll();
            System.out.println("Todos os registros de Transferencia foram removidos com sucesso.");
        } catch (PersistenciaDawException e) {
            System.err.println("Erro ao remover as transferências:");
            e.printStackTrace();
        }
    }
}
