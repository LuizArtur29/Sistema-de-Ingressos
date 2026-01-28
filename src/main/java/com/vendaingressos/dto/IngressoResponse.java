package com.vendaingressos.dto;

import com.vendaingressos.model.Ingresso;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class IngressoResponse {

    private Long idIngresso;
    private Double preco;
    private boolean ingressoDisponivel;
    private Long sessaoEventoId;
    private Long idTipoIngresso;
    private String nomeTipoIngresso;

    public IngressoResponse(Ingresso ingresso) {
        this.idIngresso = ingresso.getIdIngresso();
        this.preco = ingresso.getPreco();
        this.ingressoDisponivel = ingresso.isIngressoDisponivel();

        if (ingresso.getSessaoEvento() != null) {
            this.sessaoEventoId = ingresso.getSessaoEvento().getIdSessao();
        }

        if (ingresso.getTipoIngresso() != null) {
            this.idTipoIngresso = ingresso.getTipoIngresso().getIdTipoIngresso();
            this.nomeTipoIngresso = ingresso.getTipoIngresso().getNomeSetor();
        }
    }
}