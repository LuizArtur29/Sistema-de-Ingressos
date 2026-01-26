package com.vendaingressos.repository.jdbc.impl;

import com.vendaingressos.model.Evento;
import com.vendaingressos.model.Ingresso;
import com.vendaingressos.model.SessaoEvento;
import com.vendaingressos.repository.jdbc.IngressoRepository;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class IngressoRepositoryJDBCImpl implements IngressoRepository {

    private final DataSource dataSource;

    public IngressoRepositoryJDBCImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void salvar(Ingresso ingresso) {
        String sql = "INSERT INTO ingresso (preco, tipo_ingresso, ingresso_disponivel, id_sessao_evento) VALUES (?, ?, ?, ?)";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setDouble(1, ingresso.getPreco());
            stmt.setString(2, ingresso.getTipoIngresso());
            stmt.setBoolean(3, ingresso.isIngressoDisponivel());
            stmt.setLong(4, ingresso.getSessaoEvento().getIdSessao());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    ingresso.setIdIngresso(generatedKeys.getLong("id_ingresso"));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar ingresso via JDBC", e);
        }
    }

    @Override
    public Ingresso buscarPorId(Long id) {
        String sql = "SELECT i.*, " +
                "se.id_sessao, se.nome_sessao, se.data_hora_sessao, se.status_sessao, " +
                "e.id AS id_evento, e.nome AS nome_evento, e.capacidade_total " +
                "FROM ingresso i " +
                "INNER JOIN sessoes_evento se ON i.id_sessao_evento = se.id_sessao " +
                "INNER JOIN eventos e ON se.id_evento = e.id " +
                "WHERE i.id_ingresso = ?";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Ingresso ingresso = new Ingresso();
                ingresso.setIdIngresso(rs.getLong("id_ingresso"));
                ingresso.setPreco(rs.getDouble("preco"));
                ingresso.setTipoIngresso(rs.getString("tipo_ingresso"));
                ingresso.setIngressoDisponivel(rs.getBoolean("ingresso_disponivel"));

                // Montar a Sessão Completa
                SessaoEvento sessao = new SessaoEvento();
                sessao.setIdSessao(rs.getLong("id_sessao"));
                sessao.setNomeSessao(rs.getString("nome_sessao"));
                sessao.setStatusSessao(rs.getString("status_sessao"));

                Timestamp ts = rs.getTimestamp("data_hora_sessao");
                if (ts != null) {
                    sessao.setDataHoraSessao(ts.toLocalDateTime());
                }

                Evento eventoPai = new Evento();
                eventoPai.setId(rs.getLong("id_evento"));
                eventoPai.setNome(rs.getString("nome_evento"));
                eventoPai.setCapacidadeTotal(rs.getInt("capacidade_total")); // <--- O Service precisa disso!

                sessao.setEventoPai(eventoPai);
                ingresso.setSessaoEvento(sessao);

                return ingresso;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar ingresso completo via JDBC", e);
        }
        return null;
    }

    @Override
    public List<Ingresso> listarTodos() {
        List<Ingresso> ingressos = new ArrayList<>();
        String sql = "SELECT * FROM ingresso";
        try (Connection conexao = dataSource.getConnection();
             Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ingressos.add(mapearIngresso(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar ingressos via JDBC", e);
        }
        return ingressos;
    }

    @Override
    public int contarIngressosPorSessao(Long sessaoEventoId) {
        String sql = "SELECT COUNT(*) AS total FROM ingresso WHERE id_sessao_evento = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, sessaoEventoId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar ingressos via JDBC", e);
        }
        return 0;
    }

    @Override
    public List<Ingresso> buscarPorSessao(Long sessaoEventoId) {
        List<Ingresso> ingressos = new ArrayList<>();
        String sql = "SELECT * FROM ingresso WHERE id_sessao_evento = ?";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, sessaoEventoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ingressos.add(mapearIngresso(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar ingressos por sessão via JDBC", e);
        }
        return ingressos;
    }

    @Override
    public void atualizar(Ingresso ingresso) {
        String sql = "UPDATE ingresso SET preco = ?, tipo_ingresso = ?, ingresso_disponivel = ? WHERE id_ingresso = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setDouble(1, ingresso.getPreco());
            stmt.setString(2, ingresso.getTipoIngresso());
            stmt.setBoolean(3, ingresso.isIngressoDisponivel());
            stmt.setLong(4, ingresso.getIdIngresso());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar ingresso via JDBC", e);
        }
    }

    @Override
    public void deletar(Long id) {
        String sql = "DELETE FROM ingresso WHERE id_ingresso = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar ingresso via JDBC", e);
        }
    }

    private Ingresso mapearIngresso(ResultSet rs) throws SQLException {
        Ingresso i = new Ingresso();
        i.setIdIngresso(rs.getLong("id_ingresso"));
        i.setPreco(rs.getDouble("preco"));
        i.setTipoIngresso(rs.getString("tipo_ingresso"));
        i.setIngressoDisponivel(rs.getBoolean("ingresso_disponivel"));

        // Mapeia a Sessão (Apenas ID para evitar loops)
        SessaoEvento sessao = new SessaoEvento();
        sessao.setIdSessao(rs.getLong("id_sessao_evento"));
        i.setSessaoEvento(sessao);

        return i;
    }
}