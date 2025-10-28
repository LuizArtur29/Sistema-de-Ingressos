package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.PersistenciaDawException;
import br.edu.ifpb.es.daw.dao.impl.CompraDAOImpl;
import br.edu.ifpb.es.daw.entities.Compra;
import br.edu.ifpb.es.daw.entities.enums.MetodoPagamento;
import br.edu.ifpb.es.daw.entities.enums.Status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MainCompraSave {
    public static void main(String[] args) {
        CompraDAOImpl dao = new CompraDAOImpl();
        Compra novaCompra = new Compra(
                LocalDateTime.now(),
                2,
                new BigDecimal(300.00),
                MetodoPagamento.CARTAO_CREDITO,
                Status.APROVADO
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