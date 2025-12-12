package br.edu.ifpb.es.daw.dao;

import br.edu.ifpb.es.daw.entities.Evento;
import br.edu.ifpb.es.daw.entities.SessaoEvento;
import br.edu.ifpb.es.daw.entities.Usuario;
import br.edu.ifpb.es.daw.entities.enums.Status;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DataGeneratorDAO {

    private EntityManagerFactory emf;

    public DataGeneratorDAO(EntityManagerFactory emf) {this.emf = emf;}

    protected EntityManager getEntityManager() {return emf.createEntityManager();}

    public void generateData() {

        //Usuários
        Usuario u1 = new Usuario(
                "João Silva Oliveira", "123.456.789-01",
                LocalDate.of(1985, 10, 20),
                "joao.silva@exemplo.com",
                "senha123Forte",
                "Rua das Flores, 100, Centro, Cidade A, PR",
                "(41) 98765-4321"
        );
        Usuario u2 = new Usuario(
                "Maria Luiza Santos",
                "98765432109",
                LocalDate.of(2002, 5, 5),
                "maria.l.santos@outlook.com",
                "ML2002!xyz",
                "Av. Brasil, 50, Bairro Novo, Cidade B, SC",
                "(48) 3333-2222"
        );
        Usuario u3 = new Usuario(
                "José Dantas",
                "111.222.333-44",
                LocalDate.of(1950, 1, 15),
                "jose.dantas50@gmail.com",
                "AntigaSenha",
                "Praça da Sé, 1, Apto 101, SP",
                "(11) 99999-0000"
        );
        Usuario u4 = new Usuario(
                "Ana Beatriz Gomes",
                "444.555.666-77",
                LocalDate.of(1998, 12, 31),
                "ana@gomes.net",
                "1aB@3cD$",
                "Fazenda Riacho Doce, S/N, Zona Rural, Cidade C, MG",
                "(31) 8888-7777"
        );
        Usuario u5 = new Usuario(
                "Carlos Eduardo",
                "000.000.000-01",
                LocalDate.of(1975, 7, 7),
                "carlos.e@mail.com",
                "carlos1975",
                "Bloco 5, Apto 203, Asa Sul, DF",
                "(61) 0000-0001"
        );
        Usuario u6 = new Usuario(
                "Fernanda Rodrigues de Souza e Albuquerque",
                "777.888.999-00",
                LocalDate.of(1990, 2, 28),
                "fer.rodrigues_2023@provedor.biz",
                "MySecretPwd$",
                "Estrada Velha, Km 10, Casa 5, BA",
                "(71) 91234-5678"
        );

        //Eventos
        Evento e1 = new Evento(
                "Tech Summit Brasil 2026",
                "Maior conferência sobre IA e Cloud da América Latina.",
                LocalDate.of(2026, 9, 10),
                LocalDate.of(2026, 9, 13),
                "Centro de Convenções Anhembi, SP",
                5000,
                Status.AGENDADO
        );
        Evento e2 = new Evento(
                "Workshop de Figma Avançado",
                "Treinamento intensivo de 8 horas para designers.",
                LocalDate.of(2025, 12, 12),
                LocalDate.of(2025, 12, 12),
                "Sala de Treinamentos, Bloco C",
                30,
                Status.PENDENTE
        );
        Evento e3 = new Evento(
                "Show da Banda X",
                "Turnê de despedida da banda, com convidados especiais.",
                LocalDate.of(2026, 3, 20),
                LocalDate.of(2026, 3, 20),
                "Estádio Municipal",
                45000,
                Status.CANCELADO
        );
        Evento e4 = new Evento(
                "Café com Empreendedores",
                "Networking informal para startups locais.",
                LocalDate.of(2024, 1, 15),
                LocalDate.of(2024, 1, 15),
                "Cafeteria Central",
                50,
                Status.APROVADO
        );
        Evento e5 = new Evento(
                "Retiro Espiritual Anual",
                "Oito dias de meditação e palestras.",
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 8),
                "Pousada da Serra, MG",
                150,
                Status.AGENDADO
        );
        Evento e6 = new Evento(
                "Reunião de Alinhamento Q4",
                "Reunião interna do time de desenvolvimento.",
                LocalDate.of(2025, 12, 15),
                LocalDate.of(2025, 12, 15),
                "Google Meet Link",
                10,
                Status.CANCELADO
        );

        SessaoEvento s1 = new SessaoEvento("Palestra Inaugural: O Futuro da Tecnologia",
                LocalDateTime.of(2026, 9, 10, 9, 30), Status.PENDENTE);
        SessaoEvento s2 = new SessaoEvento("Cerimônia de Encerramento e Premiação",
                LocalDateTime.of(2024, 1, 15, 21, 0), Status.APROVADO);
        SessaoEvento s3 = new SessaoEvento("Mesa Redonda: Impacto Social da IA",
                LocalDateTime.of(2026, 9, 11, 14, 0), Status.CANCELADO);
        SessaoEvento s4 = new SessaoEvento("Sessão de Perguntas e Respostas",
                LocalDateTime.of(2025, 12, 12, 17, 30), Status.PENDENTE);
        SessaoEvento s5 = new SessaoEvento("Treinamento Prático de Cibersegurança",
                LocalDateTime.of(2026, 1, 5, 11, 0), Status.AGENDADO);
        SessaoEvento s6 = new SessaoEvento("Manutenção de Servidores (Noturno)",
                LocalDateTime.of(2026, 1, 1, 2, 0), Status.AGENDADO);
        SessaoEvento s7 = new SessaoEvento("Almoço de Negócios e Conexão de Patrocinadores",
                LocalDateTime.of(2024, 12, 25, 12, 0), Status.APROVADO);
        SessaoEvento s8 = new SessaoEvento("Revisão Final de Protótipos",
                LocalDateTime.of(2026, 2, 28, 16, 45), Status.APROVADO);

        e1.addSessao(s1);
        e2.addSessao(s3);
        e1.addSessao(s8);

        e2.addSessao(s5);

        e4.addSessao(s7);

        e5.addSessao(s6);

        e6.addSessao(s2);
        e6.addSessao(s4);

        try(EntityManager em = getEntityManager()) {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();
            try {
                em.persist(u1);
                em.persist(u2);
                em.persist(u3);
                em.persist(u4);
                em.persist(u5);
                em.persist(u6);

                em.persist(e1);
                em.persist(e2);
                em.persist(e3);
                em.persist(e4);
                em.persist(e5);
                em.persist(e6);

                em.persist(s1);
                em.persist(s2);
                em.persist(s3);
                em.persist(s4);
                em.persist(s5);
                em.persist(s6);
                em.persist(s7);
                em.persist(s8);

                transaction.commit();
            } catch (PersistenceException pe) {
                pe.printStackTrace();
                if (transaction.isActive()) {
                    transaction.rollback();
                }
            }

        }

    }

}