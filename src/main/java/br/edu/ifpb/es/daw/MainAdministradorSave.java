package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.PersistenciaDawException;
import br.edu.ifpb.es.daw.dao.impl.AdministradorDAOImpl;
import br.edu.ifpb.es.daw.entities.Administrador;

public class MainAdministradorSave {
    public static void main(String[] args) {
        AdministradorDAOImpl dao = new AdministradorDAOImpl();
        Administrador admin = new Administrador(
                "Admin Mestre",
                "admin" + System.nanoTime() + "@sistema.com",
                "senha-admin-123",
                "83999990000"
        );

        try {
            dao.save(admin);
            System.out.println("Administrador salvo com sucesso! ID: " + admin.getIdAdmin());
        } catch (PersistenciaDawException e) {
            System.err.println("Erro ao salvar o administrador:");
            e.printStackTrace();
        }
    }
}
