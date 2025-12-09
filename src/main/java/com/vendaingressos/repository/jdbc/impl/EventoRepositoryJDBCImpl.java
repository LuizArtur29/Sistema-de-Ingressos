package com.vendaingressos.repository.jdbc.impl;

import com.vendaingressos.model.Evento;
import com.vendaingressos.model.Usuario;
import com.vendaingressos.repository.jdbc.EventoRepository;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EventoRepositoryJDBCImpl implements EventoRepository {

    private final DataSource dataSource;

    public EventoRepositoryJDBCImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void salvar(Evento evento) {
        String sql = "INSERT INTO eventos (nome, descricao, data_inicio, data_fim, local, capacidade_total, status, admin_id, imagem_nome) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, evento.getNome());
            stmt.setString(2, evento.getDescricao());
            stmt.setDate(3, Date.valueOf(evento.getDataInicio()));
            stmt.setDate(4, Date.valueOf(evento.getDataFim()));
            stmt.setString(5, evento.getLocal());
            stmt.setInt(6, evento.getCapacidadeTotal());
            stmt.setString(7, evento.getStatus());

            if (evento.getAdministrador() != null) {
                stmt.setLong(8, evento.getAdministrador().getIdUsuario());
            } else {
                throw new RuntimeException("Erro: Tentativa de criar evento sem Admin vinculado.");
            }

            stmt.setString(9, evento.getImagemNome()); // Pode ser null

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    evento.setId(generatedKeys.getLong(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar evento via JDBC", e);
        }
    }

    @Override
    public Evento buscarPorId(Long id) {
        String sql = "SELECT * FROM eventos WHERE id = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapearEvento(rs);
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
        try (Connection conexao = dataSource.getConnection();
             Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                eventos.add(mapearEvento(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar eventos via JDBC", e);
        }
        return eventos;
    }

    @Override
    public void atualizar(Evento evento) {
        String sql = "UPDATE eventos SET nome = ?, descricao = ?, data_inicio = ?, data_fim = ?, local = ?, " +
                "capacidade_total = ?, status = ?, imagem_nome = ? WHERE id = ?";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, evento.getNome());
            stmt.setString(2, evento.getDescricao());
            stmt.setDate(3, Date.valueOf(evento.getDataInicio()));
            stmt.setDate(4, Date.valueOf(evento.getDataFim()));
            stmt.setString(5, evento.getLocal());
            stmt.setInt(6, evento.getCapacidadeTotal());
            stmt.setString(7, evento.getStatus());
            stmt.setString(8, evento.getImagemNome());
            stmt.setLong(9, evento.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar evento via JDBC", e);
        }
    }

    @Override
    public void deletar(Long id) {
        String sql = "DELETE FROM eventos WHERE id = ?";
        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar evento via JDBC", e);
        }
    }

    @Override
    public Double calcularReceitaTotal(Long idEvento) {
        // Query Complexa: Soma o valor_total da tabela COMPRA,
        // fazendo JOIN com INGRESSO e SESSOES_EVENTO para chegar no EVENTO correto.
        String sql = "SELECT COALESCE(SUM(c.valor_total), 0) " +
                "FROM compra c " +
                "INNER JOIN ingresso i ON c.id_ingresso = i.id_ingresso " +
                "INNER JOIN sessoes_evento se ON i.id_sessao_evento = se.id_sessao " +
                "WHERE se.id_evento = ?";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, idEvento);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            }
            return 0.0;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao calcular receita do evento via JDBC", e);
        }
    }

    private Evento mapearEvento(ResultSet rs) throws SQLException {
        Evento evento = new Evento();
        evento.setId(rs.getLong("id"));
        evento.setNome(rs.getString("nome"));
        evento.setDescricao(rs.getString("descricao"));

        if (rs.getDate("data_inicio") != null)
            evento.setDataInicio(rs.getDate("data_inicio").toLocalDate());

        if (rs.getDate("data_fim") != null)
            evento.setDataFim(rs.getDate("data_fim").toLocalDate());

        evento.setLocal(rs.getString("local"));
        evento.setCapacidadeTotal(rs.getInt("capacidade_total"));
        evento.setStatus(rs.getString("status"));
        evento.setImagemNome(rs.getString("imagem_nome"));

        Usuario admin = new Usuario();
        admin.setIdUsuario(rs.getLong("admin_id"));
        evento.setAdministrador(admin);

        return evento;
    }
}