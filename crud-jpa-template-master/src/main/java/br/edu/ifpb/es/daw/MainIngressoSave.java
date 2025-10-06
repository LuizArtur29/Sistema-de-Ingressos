package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.PersistenciaDawException;
import br.edu.ifpb.es.daw.dao.impl.IngressoDAOImpl;
import br.edu.ifpb.es.daw.entities.Ingresso;

public class MainIngressoSave {
    public static void main(String[] args) {
        IngressoDAOImpl dao = new IngressoDAOImpl();
        Ingresso novoIngresso = new Ingresso(
                true
        );

        try {
            dao.save(novoIngresso);
            System.out.println("Ingresso salvo com sucesso! ID: " + novoIngresso.getIdIngresso());
        } catch (PersistenciaDawException e) {
            System.err.println("Erro ao salvar o ingresso:");
            e.printStackTrace();
        }
    }
}
