package com.vendaingressos;

import com.vendaingressos.model.*;
import com.vendaingressos.repository.jdbc.impl.*;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class TesteCompra {

    public static void main(String[] args) {
        try {
            // 1. Configuração do Banco de Dados
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("org.postgresql.Driver");
            dataSource.setUrl("jdbc:postgresql://ep-dark-surf-ac8ztbpv.sa-east-1.aws.neon.tech/neondb?sslmode=require&channelBinding=require");
            dataSource.setUsername("neondb_owner");
            dataSource.setPassword("npg_CabVHZS10IdL");

            // 2. Instanciação dos Repositórios
            UsuarioRepositoryJDBCImpl usuarioRepo = new UsuarioRepositoryJDBCImpl(dataSource);
            EventoRepositoryJDBCImpl eventoRepo = new EventoRepositoryJDBCImpl(dataSource);
            SessaoEventoRepositoryJDBCImpl sessaoRepo = new SessaoEventoRepositoryJDBCImpl(dataSource);
            IngressoRepositoryJDBCImpl ingressoRepo = new IngressoRepositoryJDBCImpl(dataSource);
            CompraRepositoryJDBCImpl compraRepo = new CompraRepositoryJDBCImpl(dataSource);

            System.out.println("🚀 Iniciando Teste de Integração JDBC...\n");

            // ---------------------------------------------------------
            // PASSO 1: Garantir que existe um Usuário (Admin/Comprador)
            // ---------------------------------------------------------
            Usuario usuario = buscarOuCriarUsuario(usuarioRepo);
            System.out.println("✅ Usuário OK: " + usuario.getNome() + " (ID: " + usuario.getIdUsuario() + ")");

            // ---------------------------------------------------------
            // PASSO 2: Garantir que existe um Evento
            // ---------------------------------------------------------
            Evento eventoCentral = buscarOuCriarEvento(eventoRepo, usuario);
            System.out.println("📍 Evento alvo: " + eventoCentral.getNome() + " em " + eventoCentral.getLocalizacao());

            // 3. Simular uma busca de alguém perto (ex: 500 metros de distância)
            GeometryFactory factory = new GeometryFactory();
            // Um ponto levemente deslocado
            Point minhaLocalizacao = factory.createPoint(new Coordinate(-46.6333, -23.5550));
            double raioDeBusca = 1000.0; // 1km

            System.out.println("\n🔍 Buscando eventos num raio de " + raioDeBusca + " metros...");
            System.out.println("   Minha posição: " + minhaLocalizacao);

            List<Evento> eventosEncontrados = eventoRepo.buscarEventoNoRaio(minhaLocalizacao, raioDeBusca);

            if (!eventosEncontrados.isEmpty()) {
                System.out.println("✅ SUCESSO! Encontramos " + eventosEncontrados.size() + " evento(s):");
                eventosEncontrados.forEach(e ->
                        System.out.println("   -> " + e.getNome() + " (ID: " + e.getId() + ")")
                );
            } else {
                System.err.println("❌ Nenhum evento encontrado (Verifique as coordenadas).");
            }

            // ---------------------------------------------------------
            // PASSO 3: Garantir que existe uma Sessão
            // ---------------------------------------------------------
            SessaoEvento sessao = buscarOuCriarSessao(sessaoRepo, eventoCentral);
            System.out.println("✅ Sessão OK: " + sessao.getNomeSessao() + " (ID: " + sessao.getIdSessao() + ")");

            // ---------------------------------------------------------
            // PASSO 4: Garantir que existe um Ingresso
            // ---------------------------------------------------------
            Ingresso ingresso = buscarOuCriarIngresso(ingressoRepo, sessao);
            System.out.println("✅ Ingresso OK: " + ingresso.getTipoIngresso() + " (ID: " + ingresso.getIdIngresso() + ")");

            // ---------------------------------------------------------
            // PASSO 5: Realizar a Compra
            // ---------------------------------------------------------
            Compra compra = new Compra();
            compra.setDataCompra(LocalDate.now());
            compra.setQuantidadeIngressos(2);
            compra.setValorTotal(ingresso.getPreco() * 2);
            compra.setMetodoPagamento("PIX");
            compra.setStatus("CONFIRMADA");
            compra.setUsuario(usuario);
            compra.setIngresso(ingresso);

            System.out.println("\n📝 Salvando compra via Repositório...");
            compraRepo.salvar(compra);
            System.out.println("🎉 Compra salva com sucesso! ID Gerado: " + compra.getIdCompra());

            // ---------------------------------------------------------
            // PASSO 6: Listar para confirmar
            // ---------------------------------------------------------
            System.out.println("\n📋 Listando compras do banco:");
            List<Compra> compras = compraRepo.listarTodos();
            compras.forEach(c ->
                    System.out.println("  🛒 Compra ID: " + c.getIdCompra() +
                            " | Cliente: " + c.getUsuario().getIdUsuario() +
                            " | Valor: R$ " + c.getValorTotal() +
                            " | Status: " + c.getStatus())
            );

        } catch (Exception e) {
            System.err.println("❌ Erro no teste: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- Métodos Auxiliares usando Repositórios ---

    private static Usuario buscarOuCriarUsuario(UsuarioRepositoryJDBCImpl repo) {
        // Tenta pegar o primeiro que encontrar, ou cria um novo
        return repo.listarTodos().stream().findFirst().orElseGet(() -> {
            System.out.println("👤 Criando novo usuário...");
            Usuario u = new Usuario();
            u.setNome("Usuario Teste JDBC");
            u.setEmail("teste.jdbc@email.com");
            u.setSenha("123456");
            u.setCpf("12345678901");
            u.setTelefone("11999999999");
            u.setEndereco("Rua Java, 100");
            u.setDataNascimento(LocalDate.of(1990, 1, 1));
            repo.salvar(u);
            return u;
        });
    }

    private static Evento buscarOuCriarEvento(EventoRepositoryJDBCImpl repo, Usuario admin) {
        return repo.listarTodos().stream().findFirst().orElseGet(() -> {
            System.out.println("📍 Criando evento com localização...");
            Evento e = new Evento();
            e.setNome("Festival PostGIS");
            e.setDescricao("Teste de Geolocalização");
            e.setDataInicio(LocalDate.now());
            e.setDataFim(LocalDate.now().plusDays(1));
            e.setLocal("Parque Central");
            e.setCapacidadeTotal(5000);
            e.setStatus("ATIVO");
            e.setAdministrador(admin);
            e.setImagemNome("geo.png");

            // CRIA O PONTO (Latitude/Longitude)
            GeometryFactory factory = new GeometryFactory();
            // Coordinate(Longitude, Latitude)
            Point ponto = factory.createPoint(new Coordinate(-46.6333, -23.5505));
            e.setLocalizacao(ponto);

            repo.salvar(e);
            return e;
        });
    }

    private static SessaoEvento buscarOuCriarSessao(SessaoEventoRepositoryJDBCImpl repo, Evento evento) {
        // Busca sessões deste evento específico
        return repo.buscarPorEventoPai(evento.getId()).stream().findFirst().orElseGet(() -> {
            System.out.println("📅 Criando nova sessão...");
            SessaoEvento s = new SessaoEvento();
            s.setNomeSessao("Palco Principal - Dia 1");
            s.setDataHoraSessao(LocalDateTime.now().plusDays(10).withHour(18));
            s.setStatusSessao("ABERTA");
            s.setEventoPai(evento);
            repo.salvar(s);
            return s;
        });
    }

    private static Ingresso buscarOuCriarIngresso(IngressoRepositoryJDBCImpl repo, SessaoEvento sessao) {
        // Busca ingressos desta sessão
        return repo.buscarPorSessao(sessao.getIdSessao()).stream().findFirst().orElseGet(() -> {
            System.out.println("🎫 Criando novo tipo de ingresso...");
            Ingresso i = new Ingresso();
            i.setPreco(150.00);
            i.setTipoIngresso("PISTA");
            i.setIngressoDisponivel(true);
            i.setSessaoEvento(sessao);
            repo.salvar(i);
            return i;
        });
    }
}