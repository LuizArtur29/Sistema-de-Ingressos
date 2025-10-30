package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.PersistenciaDawException;
import br.edu.ifpb.es.daw.dao.impl.*;
import br.edu.ifpb.es.daw.entities.*;
import br.edu.ifpb.es.daw.entities.enums.MetodoPagamento;
import br.edu.ifpb.es.daw.entities.enums.Status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MainCompraSave {

    public static void main(String[] args) {
        UsuarioDAOImpl usuarioDAO = new UsuarioDAOImpl();
        TipoIngressoDAOImpl tipoIngressoDAO = new TipoIngressoDAOImpl();
        CompraDAOImpl compraDAO = new CompraDAOImpl();

        try {
            Usuario usuario = usuarioDAO.getByID(1L);
            TipoIngresso tipoPista = tipoIngressoDAO.getByID(1L);
            //TipoIngresso tipoVip = tipoIngressoDAO.getByID(2L);

            if (usuario == null || tipoPista == null) {
                System.err.println("ERRO: Usuário ou Tipos de Ingresso não encontrados (Execute os Mains correspondentes primeiro).");
                return;
            }

            Ingresso ingresso1 = new Ingresso(
                    tipoPista.getPreco(),
                    true
            );
            ingresso1.setTipoIngresso(tipoPista);

            /*Ingresso ingresso2 = new Ingresso(
                    tipoVip.getPreco(),
                    true
            );
            ingresso2.setTipoIngresso(tipoVip);*/

            double valorTotalCalculado = ingresso1.getPreco();
            Compra novaCompra = new Compra(
                    LocalDateTime.of(2025,6,15,18,32),
                    2,
                    new BigDecimal(valorTotalCalculado),
                    MetodoPagamento.PIX,
                    Status.APROVADO
            );

            novaCompra.setUsuario(usuario);

            novaCompra.addIngresso(ingresso1);
            //novaCompra.addIngresso(ingresso2);

            compraDAO.save(novaCompra);

            System.out.println("Compra salva com sucesso! ID: " + novaCompra.getIdCompra());
            System.out.println("  - Usuário: " + novaCompra.getUsuario().getNome());
            System.out.println("  - Valor Total: " + novaCompra.getValorTotal());
            System.out.println("  - Ingressos salvos (" + novaCompra.getQuantidadeIngressos() + "):");
            for(Ingresso ing : novaCompra.getIngressos()) {
                System.out.println("    - Ingresso ID: " + ing.getIdIngresso() +
                        ", Tipo: " + ing.getTipoIngresso().getNomeSetor() +
                        ", Preço Registrado: " + ing.getPreco() +
                        ", Compra FK: " + ing.getCompra().getIdCompra());
            }

        } catch (PersistenciaDawException e) {
            System.err.println("Erro ao salvar a compra:");
            e.printStackTrace();
        }
    }
}