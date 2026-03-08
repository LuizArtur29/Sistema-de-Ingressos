package com.vendaingressos.dto;

import com.vendaingressos.model.enums.MetodoPagamento;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

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

    @NotNull(message = "O método de pagamento não pode ser nulo")
    public MetodoPagamento metodoPagamento;

    public boolean isMeiaEntrada;

    public CompraRequest() {}

    public CompraRequest(Long usuarioID, Long ingressoID, int quantidadeIngressos, MetodoPagamento metodoPagamento, boolean isMeiaEntrada) {
        this.usuarioID = usuarioID;
        this.ingressoID = ingressoID;
        this.quantidadeIngressos = quantidadeIngressos;
        this.metodoPagamento = metodoPagamento;
        this.isMeiaEntrada = isMeiaEntrada;
    }

}
