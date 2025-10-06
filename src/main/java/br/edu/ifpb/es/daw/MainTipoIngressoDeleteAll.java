package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.PersistenciaDawException;
import br.edu.ifpb.es.daw.dao.impl.TipoIngressoDAOImpl;

public class MainTipoIngressoDeleteAll {
    public static void main(String[] args) {
        TipoIngressoDAOImpl dao = new TipoIngressoDAOImpl();
        try {
            dao.deleteAll();
            System.out.println("Todos os registros de TipoIngresso foram removidos com sucesso.");
        } catch (PersistenciaDawException e) {
            System.err.println("Erro ao remover os tipos de ingresso:");
            e.printStackTrace();
        }
    }
}
