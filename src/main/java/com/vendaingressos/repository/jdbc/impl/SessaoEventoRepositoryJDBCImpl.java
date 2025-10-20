package com.vendaingressos.repository.jdbc.impl;

import com.vendaingressos.model.SessaoEvento;
import com.vendaingressos.repository.jdbc.SessaoEventoRepositoryJDBC;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SessaoEventoRepositoryJDBCImpl implements SessaoEventoRepositoryJDBC {

    private Connection conexao;

    public SessaoEventoRepositoryJDBCImpl() {
        this.conexao = conexao;
    }

    @Override
    public void salvar(SessaoEvento sessaoEvento) {
        String sql = "INSERT INTO sessao_evento (data_hora, id_evento_pai) VALUES (?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(sessaoEvento.getDataHoraSessao()));
            stmt.setLong(2, sessaoEvento.getEventoPai().getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar sessão via JDBC", e);
        }
    }

    @Override
    public SessaoEvento buscarPorId(Long id) {
        String sql = "SELECT * FROM sessao_evento WHERE id_sessao = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                SessaoEvento s = new SessaoEvento();
                s.setIdSessao(rs.getLong("id_sessao"));
                s.setDataHoraSessao(rs.getTimestamp("data_hora").toLocalDateTime());
                return s;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar sessão via JDBC", e);
        }
        return null;
    }

    @Override
    public List<SessaoEvento> listarTodos() {
        List<SessaoEvento> lista = new ArrayList<>();
        String sql = "SELECT * FROM sessao_evento";
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                SessaoEvento s = new SessaoEvento();
                s.setIdSessao(rs.getLong("id_sessao"));
                s.setDataHoraSessao(rs.getTimestamp("data_hora").toLocalDateTime());
                lista.add(s);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar sessões via JDBC", e);
        }
        return lista;
    }

    @Override
    public List<SessaoEvento> buscarPorEventoPai(Long eventoPaiId) {
        String sql = "SELECT * FROM sessao_evento WHERE id_evento_pai = ?";
        List<SessaoEvento> lista = new ArrayList<>();
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, eventoPaiId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                SessaoEvento s = new SessaoEvento();
                s.setIdSessao(rs.getLong("id_sessao"));
                s.setDataHoraSessao(rs.getTimestamp("data_hora").toLocalDateTime());
                lista.add(s);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar sessões por evento pai via JDBC", e);
        }
        return lista;
    }

    @Override
    public void atualizar(SessaoEvento sessaoEvento) {
        String sql = "UPDATE sessao_evento SET data_hora = ? WHERE id_sessao = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(sessaoEvento.getDataHoraSessao()));
            stmt.setLong(2, sessaoEvento.getIdSessao());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar sessão via JDBC", e);
        }
    }

    @Override
    public void deletar(Long id) {
        String sql = "DELETE FROM sessao_evento WHERE id_sessao = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar sessão via JDBC", e);
        }
    }
}
