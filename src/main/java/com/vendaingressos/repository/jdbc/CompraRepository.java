package com.vendaingressos.repository.jdbc;

import com.vendaingressos.model.Compra;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CompraRepository {
    void salvar(Compra compra);
    Compra buscarPorId(Long id);
    List<Compra> listarTodos();
    void deletar(Long id);
    void atualizar(Compra compra);

    void atualizarStatus(Long id, String status);
    List<Compra> buscarPorUsuario(Long usuarioId);
    int contarIngressosVendidosPorSessao(Long sessaoEventoId);
}
