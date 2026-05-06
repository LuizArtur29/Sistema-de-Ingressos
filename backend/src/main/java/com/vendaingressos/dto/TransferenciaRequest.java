package com.vendaingressos.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record TransferenciaRequest(
        @NotNull(message = "ingressoId é obrigatório")
        Long ingressoId,

        @NotNull(message = "compradorId é obrigatório")
        Long compradorId,

        @NotNull(message = "valorRevenda é obrigatório")
        @Min(value = 0, message = "valorRevenda deve maior ou igual a zero")
        Double valorRevenda
) {
}
