package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.PersistenciaDawException;
import br.edu.ifpb.es.daw.dao.impl.UsuarioDAOImpl;

public class MainUsuarioDeleteAll {
    public static void main(String[] args) {
        UsuarioDAOImpl dao = new UsuarioDAOImpl();

        try {
            dao.deleteAll();
            System.out.println("Todos os registros de Usuario foram removidos com sucesso.");
        } catch (PersistenciaDawException e) {
            System.err.println("Erro ao remover os usuários:");
            e.printStackTrace();
        }
    }
}
