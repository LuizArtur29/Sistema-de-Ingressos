package com.vendaingressos.dto;

import com.vendaingressos.model.Ingresso;
import lombok.Data;

@Data
public class IngressoResponse {

    private Long idIngresso;
    private String tipoIngresso;
    private double preco;
    private boolean ingressoDisponivel;
    private Long sessaoEventoId;

    public IngressoResponse(Ingresso ingresso) {
        this.idIngresso = ingresso.getIdIngresso();
        this.tipoIngresso = ingresso.getTipoIngresso();
        this.preco = ingresso.getPreco();
        this.ingressoDisponivel = ingresso.isIngressoDisponivel();
        this.sessaoEventoId = ingresso.getSessaoEvento().getIdSessao();
    }
}
