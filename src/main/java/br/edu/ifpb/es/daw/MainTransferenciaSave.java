package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.PersistenciaDawException;
import br.edu.ifpb.es.daw.dao.impl.*;
import br.edu.ifpb.es.daw.entities.*;

public class MainTransferenciaSave {
    public static void main(String[] args) {
        UsuarioDAOImpl usuarioDAO = new UsuarioDAOImpl();
        IngressoDAOImpl ingressoDAO = new IngressoDAOImpl();
        TransferenciaDAOImpl transferenciaDAO = new TransferenciaDAOImpl();

        try {
            Usuario vendedor = usuarioDAO.getByID(1L);
            Usuario comprador = usuarioDAO.getByID(2L);
            Ingresso ingressoParaTransferir = ingressoDAO.findByIdComTipoIngresso(1L);

            if (vendedor == null || comprador == null || ingressoParaTransferir == null) {
                System.err.println("ERRO: Execute os Main Saves anteriores para criar Usuários com IDs 1 e 2, e um Ingresso com ID 1.");
                return;
            }
            System.out.println("Usando Vendedor ID: " + vendedor.getIdUsuario());
            System.out.println("Usando Comprador ID: " + comprador.getIdUsuario());
            System.out.println("Transferindo Ingresso ID: " + ingressoParaTransferir.getIdIngresso());

            if (ingressoParaTransferir.getCompra() == null || !ingressoParaTransferir.getCompra().getUsuario().equals(vendedor)) {
                System.out.println("AVISO: Ingresso não pertence originalmente ao vendedor (para fins de teste).");
            }

            Transferencia transferencia = new Transferencia(
                    ingressoParaTransferir.getTipoIngresso().getPreco() * 1.1
            );

            transferencia.setVendedor(vendedor);
            transferencia.setComprador(comprador);
            transferencia.setIngressoTransferido(ingressoParaTransferir);

            transferenciaDAO.save(transferencia);

            System.out.println("Transferência salva com sucesso! ID: " + transferencia.getIdTransferencia());
            System.out.println("  - Vendedor ID: " + transferencia.getVendedor().getIdUsuario());
            System.out.println("  - Comprador ID: " + transferencia.getComprador().getIdUsuario());
            System.out.println("  - Ingresso Transferido ID: " + transferencia.getIngressoTransferido().getIdIngresso());
            System.out.println("  - Valor Revenda: " + transferencia.getValorRevenda());

        } catch (PersistenciaDawException e) {
            System.err.println("Erro ao salvar a transferência:");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Ocorreu um erro inesperado:");
            e.printStackTrace();
        }
    }
}