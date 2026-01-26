package com.vendaingressos.repository.jdbc.impl;

import com.vendaingressos.model.Evento;
import com.vendaingressos.model.SessaoEvento;
import com.vendaingressos.repository.jdbc.SessaoEventoRepository;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SessaoEventoRepositoryJDBCImpl implements SessaoEventoRepository {

    private final DataSource dataSource;

    public SessaoEventoRepositoryJDBCImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void salvar(SessaoEvento sessaoEvento) {
        String sql = "INSERT INTO sessoes_evento (id_evento, data_hora_sessao, nome_sessao, status_sessao) VALUES (?, ?, ?, ?)";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, sessaoEvento.getEventoPai().getId());
            stmt.setTimestamp(2, Timestamp.valueOf(sessaoEvento.getDataHoraSessao()));
            stmt.setString(3, sessaoEvento.getNomeSessao());
            stmt.setString(4, sessaoEvento.getStatusSessao());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    sessaoEvento.setIdSessao(generatedKeys.getLong("id_sessao"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar sessão de evento via JDBC", e);
        }
    }

    @Override
    public SessaoEvento buscarPorId(Long id) {
        String sql = "SELECT * FROM sessoes_evento WHERE id_sessao = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearSessao(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar sessão via JDBC", e);
        }
        return null;
    }

    @Override
    public List<SessaoEvento> listarTodos() {
        List<SessaoEvento> sessoes = new ArrayList<>();
        String sql = "SELECT * FROM sessoes_evento";
        try (Connection conexao = dataSource.getConnection();
             Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                sessoes.add(mapearSessao(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar sessões via JDBC", e);
        }
        return sessoes;
    }

    @Override
    public List<SessaoEvento> buscarPorEventoPai(Long eventoId) {
        List<SessaoEvento> sessoes = new ArrayList<>();
        String sql = "SELECT * FROM sessoes_evento WHERE id_evento = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, eventoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                sessoes.add(mapearSessao(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar sessões do evento via JDBC", e);
        }
        return sessoes;
    }

    @Override
    public void atualizar(SessaoEvento sessaoEvento) {
        String sql = "UPDATE sessoes_evento SET data_hora_sessao = ?, nome_sessao = ?, status_sessao = ? WHERE id_sessao = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(sessaoEvento.getDataHoraSessao()));
            stmt.setString(2, sessaoEvento.getNomeSessao());
            stmt.setString(3, sessaoEvento.getStatusSessao());
            stmt.setLong(4, sessaoEvento.getIdSessao());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar sessão via JDBC", e);
        }
    }

    @Override
    public void deletar(Long id) {
        String sql = "DELETE FROM sessoes_evento WHERE id_sessao = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar sessão via JDBC", e);
        }
    }

    private SessaoEvento mapearSessao(ResultSet rs) throws SQLException {
        SessaoEvento s = new SessaoEvento();
        s.setIdSessao(rs.getLong("id_sessao"));
        s.setNomeSessao(rs.getString("nome_sessao"));
        s.setStatusSessao(rs.getString("status_sessao"));

        Timestamp ts = rs.getTimestamp("data_hora_sessao");
        if (ts != null) {
            s.setDataHoraSessao(ts.toLocalDateTime());
        }

        Evento evento = new Evento();
        evento.setId(rs.getLong("id_evento"));
        s.setEventoPai(evento);

        return s;
    }
}