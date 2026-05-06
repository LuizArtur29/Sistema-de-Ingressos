package com.vendaingressos.dto;

import com.vendaingressos.model.Transferencia;

public record TransferenciaResponse(
        Long idTransferencia,
        Long ingressoId,
        Long vendedorId,
        Long compradorId,
        Double valorRevenda
) {
    public TransferenciaResponse(Transferencia transferencia) {
        this(
                transferencia.getIdTransferencia(),
                transferencia.getIngressoTransferido() != null ? transferencia.getIngressoTransferido().getIdIngresso() : null,
                transferencia.getVendedor() != null ? transferencia.getVendedor().getIdUsuario() : null,
                transferencia.getComprador() != null ? transferencia.getComprador().getIdUsuario() : null,
                transferencia.getValorRevenda()
        );
    }
}