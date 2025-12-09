package com.vendaingressos.repository.jdbc;

import com.vendaingressos.model.Evento;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventoRepository {
    void salvar(Evento evento);
    Evento buscarPorId(Long id);
    List<Evento> listarTodos();
    void atualizar(Evento evento);
    void deletar(Long id);

    @Query(value = "SELECT calcular_receita_evento(:idEvento)", nativeQuery = true)
    Double calcularReceitaTotal(@Param("idEvento") Long idEvento);
}
