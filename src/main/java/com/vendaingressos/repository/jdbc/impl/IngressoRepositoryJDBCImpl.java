package com.vendaingressos.repository.jdbc.impl;

import com.vendaingressos.model.Ingresso;
import com.vendaingressos.repository.jdbc.IngressoRepositoryJDBC;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class IngressoRepositoryJDBCImpl implements IngressoRepositoryJDBC {

    private Connection conexao;

    public IngressoRepositoryJDBCImpl() {
        this.conexao = conexao;
    }

    @Override
    public void salvar(Ingresso ingresso) {
        String sql = "INSERT INTO ingresso (preco, tipo, id_sessao) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setDouble(1, ingresso.getPreco());
            stmt.setString(2, ingresso.getTipoIngresso());
            stmt.setLong(3, ingresso.getSessaoEvento().getIdSessao());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar ingresso via JDBC", e);
        }
    }

    @Override
    public Ingresso buscarPorId(Long id) {
        String sql = "SELECT * FROM ingresso WHERE id_ingresso = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Ingresso i = new Ingresso();
                i.setIdIngresso(rs.getLong("id_ingresso"));
                i.setPreco(rs.getDouble("preco"));
                i.setTipoIngresso(rs.getString("tipo"));
                return i;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar ingresso via JDBC", e);
        }
        return null;
    }

    @Override
    public List<Ingresso> listarTodos() {
        List<Ingresso> ingressos = new ArrayList<>();
        String sql = "SELECT * FROM ingresso";
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Ingresso i = new Ingresso();
                i.setIdIngresso(rs.getLong("id_ingresso"));
                i.setPreco(rs.getDouble("preco"));
                i.setTipoIngresso(rs.getString("tipo"));
                ingressos.add(i);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar ingressos via JDBC", e);
        }
        return ingressos;
    }

    @Override
    public Long contarPorSessaoEvento(Long sessaoEventoId) {
        String sql = "SELECT COUNT(*) AS total FROM ingresso WHERE id_sessao = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, sessaoEventoId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getLong("total");
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar ingressos via JDBC", e);
        }
        return 0L;
    }

    @Override
    public void atualizar(Ingresso ingresso) {
        String sql = "UPDATE ingresso SET preco = ?, tipo = ? WHERE id_ingresso = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setDouble(1, ingresso.getPreco());
            stmt.setString(2, ingresso.getTipoIngresso());
            stmt.setLong(3, ingresso.getIdIngresso());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar ingresso via JDBC", e);
        }
    }

    @Override
    public void deletar(Long id) {
        String sql = "DELETE FROM ingresso WHERE id_ingresso = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar ingresso via JDBC", e);
        }
    }
}
