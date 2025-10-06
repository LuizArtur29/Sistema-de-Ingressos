package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.PersistenciaDawException;
import br.edu.ifpb.es.daw.dao.impl.CompraDAOImpl;

public class MainCompraDeleteAll {
    public static void main(String[] args) {
        CompraDAOImpl dao = new CompraDAOImpl();
        try {
            dao.deleteAll();
            System.out.println("Todos os registros de Compra foram removidos com sucesso.");
        } catch (PersistenciaDawException e) {
            System.err.println("Erro ao remover as compras:");
            e.printStackTrace();
        }
    }
}