package com.vendaingressos.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CompraRequest {

    Long usuarioID;
    Long ingressoID;
    public int quantidadeIngressos;
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
