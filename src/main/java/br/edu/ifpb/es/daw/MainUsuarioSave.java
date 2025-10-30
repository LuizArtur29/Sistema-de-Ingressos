package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.PersistenciaDawException;
import br.edu.ifpb.es.daw.dao.impl.UsuarioDAOImpl;
import br.edu.ifpb.es.daw.entities.Usuario;
import java.time.LocalDate;

public class MainUsuarioSave {
    public static void main(String[] args) {
        UsuarioDAOImpl dao = new UsuarioDAOImpl();
        Usuario novoUsuario1 = new Usuario(
                "Cliente Fiel",
                String.valueOf(System.nanoTime()).substring(0, 11),
                LocalDate.of(1995, 8, 22),
                "cliente" + System.nanoTime() + "@email.com",
                "senhaCliente789",
                "Avenida Principal, 456",
                "83977665544"
        );

                Usuario novoUsuario2 = new Usuario(
                "Cliente Maravilhoso",
                String.valueOf(System.nanoTime()).substring(0, 11),
                LocalDate.of(2000, 1, 26),
                "drake" + System.nanoTime() + "@email.com",
                "senha8790",
                "Avenida , 777",
                "83997655894"
        );

        try {
            dao.save(novoUsuario1);
            dao.save(novoUsuario2);
            System.out.println("Usuário salvo com sucesso! ID: " + novoUsuario1.getIdUsuario());
            System.out.println("Usuário salvo com sucesso! ID: " + novoUsuario2.getIdUsuario());
        } catch (PersistenciaDawException e) {
            System.err.println("Erro ao salvar o usuário:");
            e.printStackTrace();
        }
    }
}