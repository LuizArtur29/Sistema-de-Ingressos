package com.vendaingressos.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CompraRequest {

    UUID usuarioID;
    UUID ingressoID;
    public int quantidadeIngressos;
    public String metodoPagamento;
    public boolean isMeiaEntrada;

    public CompraRequest() {}

    public CompraRequest(UUID usuarioID, UUID ingressoID, int quantidadeIngressos, String metodoPagamento, boolean isMeiaEntrada) {
        this.usuarioID = usuarioID;
        this.ingressoID = ingressoID;
        this.quantidadeIngressos = quantidadeIngressos;
        this.metodoPagamento = metodoPagamento;
        this.isMeiaEntrada = isMeiaEntrada;
    }


}
