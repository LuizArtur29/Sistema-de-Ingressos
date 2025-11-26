package com.vendaingressos;

import com.vendaingressos.model.Compra;
import com.vendaingressos.model.Ingresso;
import com.vendaingressos.model.Usuario;
import com.vendaingressos.repository.jdbc.impl.CompraRepositoryJDBCImpl;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class TesteCompra {
    public static void main(String[] args) {
        try {
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("org.postgresql.Driver");
            dataSource.setUrl("jdbc:postgresql://ep-dark-surf-ac8ztbpv.sa-east-1.aws.neon.tech/neondb?sslmode=require&channelBinding=require");
            dataSource.setUsername("neondb_owner");
            dataSource.setPassword("npg_CabVHZS10IdL");

            Long ingressoId = buscarOuCriarIngresso(dataSource);

            if (ingressoId == null) {
                System.err.println("❌ Não foi possível obter um ingresso!");
                return;
            }

            System.out.println("✅ Usando ingresso ID: " + ingressoId);

            CompraRepositoryJDBCImpl compraRepo = new CompraRepositoryJDBCImpl(dataSource);

            Compra compra = new Compra();
            compra.setDataCompra(LocalDate.now());
            compra.setQuantidadeIngressos(2);
            compra.setValorTotal(200.0);
            compra.setMetodoPagamento("PIX");
            compra.setStatus("CONFIRMADA");

            Usuario usuario = new Usuario();
            usuario.setIdUsuario(1L);
            compra.setUsuario(usuario);

            Ingresso ingresso = new Ingresso();
            ingresso.setIdIngresso(ingressoId);
            compra.setIngresso(ingresso);

            System.out.println("\n📝 Salvando compra...");
            compraRepo.salvar(compra);
            System.out.println("✅ Compra salva com sucesso!");

            System.out.println("\n📋 Listando todas as compras:");
            compraRepo.listarTodos().forEach(c ->
                    System.out.println("  - Compra ID: " + c.getIdCompra() +
                            " | Valor: R$ " + c.getValorTotal() +
                            " | Status: " + c.getStatus())
            );

        } catch (Exception e) {
            System.err.println("❌ Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Long buscarOuCriarIngresso(DataSource dataSource) {
        try (Connection conn = dataSource.getConnection()) {

            String sqlBuscar = "SELECT id_ingresso FROM ingresso WHERE ingresso_disponivel = true LIMIT 1";
            try (PreparedStatement stmt = conn.prepareStatement(sqlBuscar);
                 ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    System.out.println("🎫 Ingresso encontrado no banco");
                    return rs.getLong("id_ingresso");
                }
            }

            System.out.println("🎫 Buscando sessão de evento...");
            Long sessaoEventoId = buscarOuCriarSessaoEvento(conn);

            if (sessaoEventoId == null) {
                System.err.println("❌ Não foi possível obter uma sessão de evento!");
                return null;
            }

            System.out.println("🎫 Criando novo ingresso...");
            String sqlCriar = "INSERT INTO ingresso (id_sessao_evento, preco, tipo_ingresso, ingresso_disponivel) " +
                    "VALUES (?, ?, ?, ?) RETURNING id_ingresso";

            try (PreparedStatement stmt = conn.prepareStatement(sqlCriar)) {
                stmt.setLong(1, sessaoEventoId);
                stmt.setDouble(2, 100.0);
                stmt.setString(3, "INTEIRA");
                stmt.setBoolean(4, true);

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    Long id = rs.getLong(1);
                    System.out.println("✅ Ingresso criado com ID: " + id);
                    return id;
                }
            }

        } catch (Exception e) {
            System.err.println("⚠️  Erro ao buscar/criar ingresso: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    private static Long buscarOuCriarSessaoEvento(Connection conn) {
        try {
            String sqlBuscar = "SELECT id_sessao FROM sessoes_evento LIMIT 1";
            try (PreparedStatement stmt = conn.prepareStatement(sqlBuscar);
                 ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    System.out.println("📅 Sessão de evento encontrada");
                    return rs.getLong("id_sessao");
                }
            }

            System.out.println("📅 Criando nova sessão de evento...");

            Long eventoId = buscarOuCriarEvento(conn);
            if (eventoId == null) return null;

            String sqlCriar = "INSERT INTO sessoes_evento (id_evento, data_hora_sessao, nome_sessao, status_sessao) " +
                    "VALUES (?, CURRENT_TIMESTAMP, ?, ?) RETURNING id_sessao";

            try (PreparedStatement stmt = conn.prepareStatement(sqlCriar)) {
                stmt.setLong(1, eventoId);
                stmt.setString(2, "Sessão Teste");
                stmt.setString(3, "ABERTA");

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    Long id = rs.getLong(1);
                    System.out.println("✅ Sessão criada com ID: " + id);
                    return id;
                }
            }

        } catch (Exception e) {
            System.err.println("⚠️  Erro com sessão: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    private static Long buscarOuCriarEvento(Connection conn) {
        try {
            String sqlBuscar = "SELECT id FROM eventos LIMIT 1";
            try (PreparedStatement stmt = conn.prepareStatement(sqlBuscar);
                 ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    System.out.println("🎭 Evento encontrado");
                    return rs.getLong("id");
                }
            }

            System.out.println("🎭 Criando novo evento...");

            String sqlCriar = "INSERT INTO eventos (nome, descricao, local, capacidade_total, data_inicio, data_fim, status) " +
                    "VALUES (?, ?, ?, ?, CURRENT_DATE, CURRENT_DATE + 5, ?) RETURNING id";

            try (PreparedStatement stmt = conn.prepareStatement(sqlCriar)) {
                stmt.setString(1, "Evento Teste");
                stmt.setString(2, "Evento criado automaticamente");
                stmt.setString(3, "Local Teste");
                stmt.setInt(4, 500);
                stmt.setString(5, "ATIVO");

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    Long id = rs.getLong(1);
                    System.out.println("✅ Evento criado com ID: " + id);
                    return id;
                }
            }

        } catch (Exception e) {
            System.err.println("⚠️  Erro com evento: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}