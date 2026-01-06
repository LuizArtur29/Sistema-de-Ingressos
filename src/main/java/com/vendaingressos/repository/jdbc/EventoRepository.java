package com.vendaingressos.repository.jdbc;

import com.vendaingressos.model.Evento;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventoRepository {
    void salvar(Evento evento);
    Evento buscarPorId(Long id);
    List<Evento> listarTodos();
    void atualizar(Evento evento);
    void deletar(Long id);

    // PL/pgsql
    @Query(value = "SELECT calcular_receita_evento(:idEvento)", nativeQuery = true)
    Double calcularReceitaTotal(@Param("idEvento") Long idEvento);

    // PostGis
    @Query(value = "SELECT * FROM evento e WHERE ST_DWithin(e.localizacao::geography, :ponto::geography, :raioMetros)", nativeQuery = true)
    List<Evento> buscarEventoNoRaio(Point ponto, double raioMetros);

    @Query(value = "SELECT ST_Distance(e.localizacao::geography, :outroPonto::geography) FROM evento e WHERE e.id = :id", nativeQuery = true)
    Double medirDistanciaEntrePontos(Long id, Point outroPonto);
}
