package com.vendaingressos.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class CompraRequest {

    @NotNull(message = "O ID do usuário não pode ser nulo")
    @Min(value = 1, message = "O ID do usuário deve ser um número positivo")
    public Long usuarioID;

    @NotNull(message = "O ID do ingresso não pode ser nulo")
    @Min(value = 1, message = "O ID do ingresso deve ser um número positivo")
    public Long ingressoID;

    @Min(value = 1, message = "A quantidade de ingressos deve ser no mínimo 1")
    public int quantidadeIngressos;

    @NotBlank(message = "O método de pagamento não pode estar em branco")
    public String metodoPagamento;

    public boolean isMeiaEntrada;

    public CompraRequest() {}

    public CompraRequest(Long usuarioID, Long ingressoID, int quantidadeIngressos, String metodoPagamento, boolean isMeiaEntrada) {
        this.usuarioID = usuarioID;
        this.ingressoID = ingressoID;
        this.quantidadeIngressos = quantidadeIngressos;
        this.metodoPagamento = metodoPagamento;
        this.isMeiaEntrada = isMeiaEntrada;
    }

}
