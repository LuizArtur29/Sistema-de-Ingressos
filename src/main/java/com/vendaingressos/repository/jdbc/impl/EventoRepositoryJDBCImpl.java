package com.vendaingressos.repository.jdbc.impl;

import com.vendaingressos.model.Evento;
import com.vendaingressos.repository.jdbc.EventoRepositoryJDBC;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EventoRepositoryJDBCImpl implements EventoRepositoryJDBC {

    private final Connection conexao;

    public EventoRepositoryJDBCImpl() {
        this.conexao = conexao;
    }

    @Override
    public void salvar(Evento evento) {
        String sql = "INSERT INTO evento (nome, descricao, data_evento, local) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, evento.getNome());
            stmt.setString(2, evento.getDescricao());
            stmt.setDate(3, Date.valueOf(evento.getDataEvento()));
            stmt.setString(4, evento.getLocal());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar evento via JDBC", e);
        }
    }

    @Override
    public Evento buscarPorId(Long id) {
        String sql = "SELECT * FROM evento WHERE id_evento = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Evento evento = new Evento();
                evento.setIdEvento(rs.getLong("id_evento"));
                evento.setNome(rs.getString("nome"));
                evento.setDescricao(rs.getString("descricao"));
                evento.setDataEvento(rs.getDate("data_evento").toLocalDate());
                evento.setLocal(rs.getString("local"));
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
        String sql = "SELECT * FROM evento";
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Evento evento = new Evento();
                evento.setIdEvento(rs.getLong("id_evento"));
                evento.setNome(rs.getString("nome"));
                evento.setDescricao(rs.getString("descricao"));
                evento.setDataEvento(rs.getDate("data_evento").toLocalDate());
                evento.setLocal(rs.getString("local"));
                eventos.add(evento);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar eventos via JDBC", e);
        }
        return eventos;
    }

    @Override
    public void atualizar(Evento evento) {
        String sql = "UPDATE evento SET nome = ?, descricao = ?, data_evento = ?, local = ? WHERE id_evento = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, evento.getNome());
            stmt.setString(2, evento.getDescricao());
            stmt.setDate(3, Date.valueOf(evento.getDataEvento()));
            stmt.setString(4, evento.getLocal());
            stmt.setLong(5, evento.getIdEvento());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar evento via JDBC", e);
        }
    }

    @Override
    public void deletar(Long id) {
        String sql = "DELETE FROM evento WHERE id_evento = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar evento via JDBC", e);
        }
    }
}
