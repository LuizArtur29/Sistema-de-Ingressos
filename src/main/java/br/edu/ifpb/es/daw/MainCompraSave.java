package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.PersistenciaDawException;
import br.edu.ifpb.es.daw.dao.impl.CompraDAOImpl;
import br.edu.ifpb.es.daw.entities.Compra;

import java.time.LocalDate;

public class MainCompraSave {
    public static void main(String[] args) {
        CompraDAOImpl dao = new CompraDAOImpl();
        Compra novaCompra = new Compra(
                LocalDate.now(),
                2,
                300.00,
                "CARTAO_CREDITO",
                "APROVADA"
        );

        try {
            dao.save(novaCompra);
            System.out.println("Compra salva com sucesso! ID: " + novaCompra.getIdCompra());
        } catch (PersistenciaDawException e) {
            System.err.println("Erro ao salvar a compra:");
            e.printStackTrace();
        }
    }
}