package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.PersistenciaDawException;
import br.edu.ifpb.es.daw.dao.impl.UsuarioDAOImpl;
import br.edu.ifpb.es.daw.entities.Usuario;

import java.time.LocalDate;

public class MainUsuarioSave {
    public static void main(String[] args) {
        UsuarioDAOImpl dao = new UsuarioDAOImpl();
        Usuario novoUsuario = new Usuario(
                "Usuário Padrão DAO",
                String.valueOf(System.nanoTime()).substring(0, 11),
                LocalDate.of(2000, 1, 1),
                "dao" + System.nanoTime() + "@exemplo.com",
                "senha123",
                "Rua Nova, 123",
                "83911223344"
        );

        try {
            dao.save(novoUsuario);
            System.out.println("Usuário salvo com sucesso! ID: " + novoUsuario.getIdUsuario());
        } catch (PersistenciaDawException e) {
            System.err.println("Erro ao salvar o usuário:");
            e.printStackTrace();
        }
    }
}
