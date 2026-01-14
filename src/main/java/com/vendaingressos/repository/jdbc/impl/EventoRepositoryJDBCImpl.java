package com.vendaingressos.repository.jdbc.impl;

import com.vendaingressos.model.Evento;
import com.vendaingressos.model.Usuario;
import com.vendaingressos.repository.jdbc.EventoRepository;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKBReader;
import org.locationtech.jts.io.WKBWriter;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EventoRepositoryJDBCImpl implements EventoRepository {

    private final DataSource dataSource;
    private final WKBWriter wkbWriter = new WKBWriter();
    private final WKBReader wkbReader = new WKBReader();

    public EventoRepositoryJDBCImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void salvar(Evento evento) {
        String sql = "INSERT INTO eventos (nome, descricao, data_inicio, data_fim, local, capacidade_total, status, admin_id, imagem_nome, localizacao) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
                throw new RuntimeException("Erro: Evento sem Admin.");
            }

            stmt.setString(9, evento.getImagemNome());

            // CONVERSÃO MANUAL DO POSTGIS (Obrigatória no JDBC)
            if (evento.getLocalizacao() != null) {
                // Converte o objeto Point para Bytes (WKB)
                byte[] geometryBytes = wkbWriter.write(evento.getLocalizacao());
                stmt.setBytes(10, geometryBytes);
            } else {
                stmt.setNull(10, Types.BINARY);
            }

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
        // CORREÇÃO: Usamos ST_AsBinary para garantir WKB compatível com JTS
        String sql = "SELECT id, nome, descricao, data_inicio, data_fim, local, capacidade_total, status, admin_id, imagem_nome, " +
                "ST_AsBinary(localizacao) as localizacao FROM eventos WHERE id = ?";

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
        List<Evento> eventos = new ArrayList<>(); // <--- ESTA LINHA ESTAVA FALTANDO

        // CORREÇÃO: Usamos ST_AsBinary para garantir WKB compatível com JTS
        String sql = "SELECT id, nome, descricao, data_inicio, data_fim, local, capacidade_total, status, admin_id, imagem_nome, " +
                "ST_AsBinary(localizacao) as localizacao FROM eventos";

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
                "capacidade_total = ?, status = ?, imagem_nome = ?, localizacao = ? WHERE id = ?";

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

            if (evento.getLocalizacao() != null) {
                byte[] geometryBytes = wkbWriter.write(evento.getLocalizacao());
                stmt.setBytes(9, geometryBytes);
            } else {
                stmt.setNull(9, Types.BINARY);
            }

            stmt.setLong(10, evento.getId());

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
        String sql = "SELECT calcular_receita_evento(?)";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, idEvento);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getDouble(1);
            return 0.0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao calcular receita", e);
        }
    }

    // Métodos extras vazios conforme sua interface original
    @Override
    public List<Evento> buscarEventoNoRaio(Point ponto, double raioMetros) {
        List<Evento> eventos = new ArrayList<>();
        // CORREÇÃO: Busca usando ST_DWithin E retorna o binário limpo com ST_AsBinary
        String sql = "SELECT id, nome, descricao, data_inicio, data_fim, local, capacidade_total, status, admin_id, imagem_nome, " +
                "ST_AsBinary(localizacao) as localizacao " +
                "FROM eventos WHERE ST_DWithin(" +
                "ST_SetSRID(localizacao, 4326)::geography, " +
                "ST_SetSRID(ST_GeomFromWKB(?), 4326)::geography, " +
                "?)";

        try (Connection conexao = dataSource.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setBytes(1, wkbWriter.write(ponto));
            stmt.setDouble(2, raioMetros);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    eventos.add(mapearEvento(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar eventos no raio via JDBC", e);
        }
        return eventos;
    }
    @Override
    public Double medirDistanciaEntrePontos(Long id, Point outroPonto) { return 0.0; }

    private Evento mapearEvento(ResultSet rs) throws SQLException {
        Evento evento = new Evento();
        evento.setId(rs.getLong("id"));
        evento.setNome(rs.getString("nome"));
        evento.setDescricao(rs.getString("descricao"));

        if (rs.getDate("data_inicio") != null) evento.setDataInicio(rs.getDate("data_inicio").toLocalDate());
        if (rs.getDate("data_fim") != null) evento.setDataFim(rs.getDate("data_fim").toLocalDate());

        evento.setLocal(rs.getString("local"));
        evento.setCapacidadeTotal(rs.getInt("capacidade_total"));
        evento.setStatus(rs.getString("status"));
        evento.setImagemNome(rs.getString("imagem_nome"));

        // LEITURA DO POSTGIS
        byte[] geomBytes = rs.getBytes("localizacao");
        if (geomBytes != null) {
            try {
                evento.setLocalizacao((Point) wkbReader.read(geomBytes));
            } catch (ParseException e) {
                System.err.println("Erro ao ler geometria: " + e.getMessage());
            }
        }

        Usuario admin = new Usuario();
        admin.setIdUsuario(rs.getLong("admin_id"));
        evento.setAdministrador(admin);

        return evento;
    }
}