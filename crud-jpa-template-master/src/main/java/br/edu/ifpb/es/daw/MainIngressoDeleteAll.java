package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.PersistenciaDawException;
import br.edu.ifpb.es.daw.dao.impl.IngressoDAOImpl;

public class MainIngressoDeleteAll {
    public static void main(String[] args) {
        IngressoDAOImpl dao = new IngressoDAOImpl();
        try {
            dao.deleteAll();
            System.out.println("Todos os registros de Ingresso foram removidos com sucesso.");
        } catch (PersistenciaDawException e) {
            System.err.println("Erro ao remover os ingressos:");
            e.printStackTrace();
        }
    }
}
