package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.PersistenciaDawException;
import br.edu.ifpb.es.daw.dao.impl.AdministradorDAOImpl;

public class MainAdministradorDeleteAll {
    public static void main(String[] args) {
        AdministradorDAOImpl dao = new AdministradorDAOImpl();
        try {
            dao.deleteAll();
            System.out.println("Todos os registros de Administrador foram removidos com sucesso.");
        } catch (PersistenciaDawException e) {
            System.err.println("Erro ao remover os administradores:");
            e.printStackTrace();
        }
    }
}