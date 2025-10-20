package com.vendaingressos.repository.jdbc.impl;

import com.vendaingressos.model.Evento;
import com.vendaingressos.repository.jdbc.EventoRepositoryJDBC;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EventoRepositoryJDBCImpl implements EventoRepositoryJDBC {

    private Connection conexao;

    public EventoRepositoryJDBCImpl() {
        this.conexao = conexao;
    }

    @Override
    public void salvar(Evento evento) {
        String sql = "INSERT INTO eventos (nome, descricao, data_inicio, data_fim, local, capacidade_total, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, evento.getNome());
            stmt.setString(2, evento.getDescricao());
            stmt.setDate(3, Date.valueOf(evento.getDataInicio()));
            stmt.setDate(4, Date.valueOf(evento.getDataFim()));
            stmt.setString(5, evento.getLocal());
            stmt.setInt(6, evento.getCapacidadeTotal());
            stmt.setString(7, evento.getStatus());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar evento via JDBC", e);
        }
    }

    @Override
    public Evento buscarPorId(Long id) {
        String sql = "SELECT * FROM eventos WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Evento evento = new Evento();
                evento.setId(rs.getLong("id"));
                evento.setNome(rs.getString("nome"));
                evento.setDescricao(rs.getString("descricao"));
                evento.setDataInicio(rs.getDate("data_inicio").toLocalDate());
                evento.setDataFim(rs.getDate("data_fim").toLocalDate());
                evento.setLocal(rs.getString("local"));
                evento.setCapacidadeTotal(rs.getInt("capacidade_total"));
                evento.setStatus(rs.getString("status"));
                return evento;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar evento via JDBC", e);
        }
        return null;
    }


    @Override
    public List<Evento> listarTodos() {
        List<Evento> eventos = new ArrayList<>();
        String sql = "SELECT * FROM eventos";
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Evento evento = new Evento();
                evento.setId(rs.getLong("id"));
                evento.setNome(rs.getString("nome"));
                evento.setDescricao(rs.getString("descricao"));
                evento.setDataInicio(rs.getDate("data_inicio").toLocalDate());
                evento.setDataFim(rs.getDate("data_fim").toLocalDate());
                evento.setLocal(rs.getString("local"));
                evento.setCapacidadeTotal(rs.getInt("capacidade_total"));
                evento.setStatus(rs.getString("status"));
                eventos.add(evento);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar eventos via JDBC", e);
        }
        return eventos;
    }

    @Override
    public void atualizar(Evento evento) {
        String sql = "UPDATE eventos SET nome = ?, descricao = ?, data_inicio = ?, data_fim = ?, local = ?, capacidade_total = ?, status = ? WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, evento.getNome());
            stmt.setString(2, evento.getDescricao());
            stmt.setDate(3, Date.valueOf(evento.getDataInicio()));
            stmt.setDate(4, Date.valueOf(evento.getDataFim()));
            stmt.setString(5, evento.getLocal());
            stmt.setInt(6, evento.getCapacidadeTotal());
            stmt.setString(7, evento.getStatus());
            stmt.setLong(8, evento.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar evento via JDBC", e);
        }
    }

    @Override
    public void deletar(Long id) {
        String sql = "DELETE FROM eventos WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar evento via JDBC", e);
        }
    }

}
