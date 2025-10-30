package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.PersistenciaDawException;
import br.edu.ifpb.es.daw.dao.impl.*;
import br.edu.ifpb.es.daw.entities.*;

public class MainIngressoSave {
    public static void main(String[] args) {
        TipoIngressoDAOImpl tipoDAO = new TipoIngressoDAOImpl();
        IngressoDAOImpl ingressoDAO = new IngressoDAOImpl();

        try {
            TipoIngresso tipo;
            try {
                tipo = tipoDAO.getByID(1L);
                if (tipo == null) {
                    SessaoEventoDAOImpl sessaoDAO = new SessaoEventoDAOImpl(); // Precisa da Sessao
                    SessaoEvento sessao = sessaoDAO.getByID(1L);
                    if (sessao == null) throw new PersistenciaDawException("Sessão 1 não encontrada", null);
                    tipo = new TipoIngresso("Pista Lote Único", 60.0, 20, 20, 1);
                    tipo.setSessao(sessao);
                    tipoDAO.save(tipo);
                }
            } catch (PersistenciaDawException findError){
                System.err.println("Erro ao buscar/criar tipo pré-requisito: " + findError.getMessage());
                return;
            }
            System.out.println("Usando TipoIngresso ID: " + tipo.getIdTipoIngresso() + " com Preço: " + tipo.getPreco());

            Ingresso ingresso = new Ingresso(
                    tipo.getPreco(),
                    true
            );

            ingresso.setTipoIngresso(tipo);

            ingressoDAO.save(ingresso);

            System.out.println("Ingresso salvo com sucesso! ID: " + ingresso.getIdIngresso());
            System.out.println("  - Tipo Associado ID: " + ingresso.getTipoIngresso().getIdTipoIngresso());
            System.out.println("  - Preço Registrado no Ingresso: " + ingresso.getPreco());

        } catch (PersistenciaDawException e) {
            System.err.println("Erro ao salvar o ingresso avulso:");
            e.printStackTrace();
        }
    }
}