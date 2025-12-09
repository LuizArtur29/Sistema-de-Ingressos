package com.vendaingressos.repository.jdbc.impl;

import com.vendaingressos.model.Compra;
import com.vendaingressos.model.Ingresso;
import com.vendaingressos.model.Usuario;
import com.vendaingressos.repository.jdbc.CompraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CompraRepositoryJDBCImpl implements CompraRepository {

    private final DataSource dataSource;

    @Autowired
    public CompraRepositoryJDBCImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void salvar(Compra compra) {
        String sql = "INSERT INTO compra (data_compra, quantidade_ingressos, valor_total, metodo_pagamento, status, id_usuario, id_ingresso) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql, new String[]{"id_compra"})) {

            stmt.setDate(1, Date.valueOf(compra.getDataCompra()));
            stmt.setInt(2, compra.getQuantidadeIngressos());
            stmt.setDouble(3, compra.getValorTotal());
            stmt.setString(4, compra.getMetodoPagamento());
            stmt.setString(5, compra.getStatus());
            stmt.setLong(6, compra.getUsuario().getIdUsuario());
            stmt.setLong(7, compra.getIngresso().getIdIngresso());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        compra.setIdCompra(generatedKeys.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar compra via JDBC", e);
        }
    }

    @Override
    public Compra buscarPorId(Long id) {
        String sql = "SELECT * FROM compra WHERE id_compra = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapearCompra(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar compra via JDBC", e);
        }
        return null;
    }

    @Override
    public List<Compra> listarTodos() {
        List<Compra> compras = new ArrayList<>();
        String sql = "SELECT * FROM compra";
        try (Connection conexao = dataSource.getConnection();
             Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                compras.add(mapearCompra(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar compras via JDBC", e);
        }
        return compras;
    }

    @Override
    public List<Compra> buscarPorUsuario(Long usuarioId) {
        List<Compra> compras = new ArrayList<>();
        String sql = "SELECT * FROM compra WHERE id_usuario = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                compras.add(mapearCompra(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar compras por usuário via JDBC", e);
        }
        return compras;
    }

    @Override
    public void atualizarStatus(Long id, String status) {
        String sql = "UPDATE compra SET status = ? WHERE id_compra = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setLong(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar status da compra via JDBC", e);
        }
    }

    @Override
    public void atualizar(Compra compra) {
        String sql = "UPDATE compra SET data_compra = ?, quantidade_ingressos = ?, valor_total = ?, metodo_pagamento = ?, status = ?, id_usuario = ?, id_ingresso = ? WHERE id_compra = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(compra.getDataCompra()));
            stmt.setInt(2, compra.getQuantidadeIngressos());
            stmt.setDouble(3, compra.getValorTotal());
            stmt.setString(4, compra.getMetodoPagamento());
            stmt.setString(5, compra.getStatus());
            stmt.setLong(6, compra.getUsuario().getIdUsuario());
            stmt.setLong(7, compra.getIngresso().getIdIngresso());
            stmt.setLong(8, compra.getIdCompra());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar compra completa via JDBC", e);
        }
    }

    @Override
    public void deletar(Long id) {
        String sql = "DELETE FROM compra WHERE id_compra = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar compra via JDBC", e);
        }
    }

    @Override
    public int contarIngressosVendidosPorSessao(Long sessaoEventoId) {
        // SQL: Soma a quantidade de ingressos em todas as compras vinculadas a ingressos daquela sessão
        String sql = "SELECT SUM(c.quantidade_ingressos) FROM compra c " +
                "INNER JOIN ingresso i ON c.id_ingresso = i.id_ingresso " +
                "WHERE i.id_sessao_evento = ?";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, sessaoEventoId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

            return 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar ingressos vendidos via JDBC", e);
        }
    }

    private Compra mapearCompra(ResultSet rs) throws SQLException {
        Compra c = new Compra();
        c.setIdCompra(rs.getLong("id_compra"));

        Date dataSql = rs.getDate("data_compra");
        if (dataSql != null) {
            c.setDataCompra(dataSql.toLocalDate());
        }

        c.setQuantidadeIngressos(rs.getInt("quantidade_ingressos"));
        c.setValorTotal(rs.getDouble("valor_total"));
        c.setMetodoPagamento(rs.getString("metodo_pagamento"));
        c.setStatus(rs.getString("status"));

        Usuario u = new Usuario();
        u.setIdUsuario(rs.getLong("id_usuario"));
        c.setUsuario(u);

        Ingresso i = new Ingresso();
        i.setIdIngresso(rs.getLong("id_ingresso"));
        c.setIngresso(i);

        return c;
    }
}