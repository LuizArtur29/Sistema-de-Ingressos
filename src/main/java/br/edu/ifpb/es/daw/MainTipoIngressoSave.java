package br.edu.ifpb.es.daw; // Certifique-se que o pacote está correto

import br.edu.ifpb.es.daw.dao.PersistenciaDawException;
import br.edu.ifpb.es.daw.dao.impl.*; // Importar DAOs
import br.edu.ifpb.es.daw.entities.*; // Importar Entidades
import br.edu.ifpb.es.daw.entities.enums.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MainTipoIngressoSave {
    public static void main(String[] args) {
        SessaoEventoDAOImpl sessaoDAO = new SessaoEventoDAOImpl();
        TipoIngressoDAOImpl tipoDAO = new TipoIngressoDAOImpl();

        try {
            SessaoEvento sessao;
            try {
                sessao = sessaoDAO.getByID(1L);
                if (sessao == null) {
                    System.out.println("Sessão 1 não encontrada, criando pré-requisitos...");
                    EventoDAOImpl eventoDAO = new EventoDAOImpl();
                    Evento evento = eventoDAO.getByID(1L);
                    if (evento == null){
                        AdministradorDAOImpl adminDAO = new AdministradorDAOImpl();
                        Administrador admin = adminDAO.getByID(1L);
                        if (admin == null){
                            admin = new Administrador("Admin Padrão TIS", "admin_tis@sist.com", "senha", "83900000010");
                            adminDAO.save(admin);
                        }
                        evento = new Evento("Evento Padrão TIS", "Desc TIS", LocalDate.of(2025,7,15), LocalDate.of(2025, 7, 17),"Local TIS", 30, Status.APROVADO);
                        evento.setAdministrador(admin);
                        eventoDAO.save(evento);
                    }
                    sessao = new SessaoEvento("Sessão Padrão TIS", LocalDateTime.of(2025,7,15,18,30), Status.APROVADO);
                    sessao.setEventoPai(evento);
                    sessaoDAO.save(sessao);
                    System.out.println("Sessão pré-requisito criada com ID: " + sessao.getIdSessao());
                }
            } catch (PersistenciaDawException findError){
                System.err.println("Erro crítico ao buscar/criar sessão pré-requisito: " + findError.getMessage());
                return;
            }
            System.out.println("Usando Sessão ID: " + sessao.getIdSessao());

            TipoIngresso tipo = new TipoIngresso(
                    "Pista - Lote Promocional",
                    75.50,
                    200,
                    200,
                    1
            );

            tipo.setSessao(sessao);
            tipoDAO.save(tipo);

            System.out.println("Tipo de Ingresso salvo com sucesso! ID: " + tipo.getIdTipoIngresso());
            System.out.println("  - Nome: " + tipo.getNomeSetor());
            System.out.println("  - Preço: " + tipo.getPreco());
            System.out.println("  - Sessão Associada ID: " + tipo.getSessao().getIdSessao());
            System.out.println("  - (Não há ingresso individual associado diretamente neste ponto)");

        } catch (PersistenciaDawException e) {
            System.err.println("Erro ao salvar o tipo de ingresso:");
            e.printStackTrace();
        }
    }
}