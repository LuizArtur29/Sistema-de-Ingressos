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
    /** {@code false} após uso na entrada; não indica se está à venda. */
    private boolean ingressoDisponivel;
    private boolean vendido;
    /** Pode ser comprado ({@code !vendido}). */
    private boolean disponivelParaCompra;
    private Long sessaoEventoId;
    private Long idTipoIngresso;
    private String nomeTipoIngresso;

    public IngressoResponse(Ingresso ingresso) {
        this.idIngresso = ingresso.getIdIngresso();
        this.preco = ingresso.getPreco();
        this.ingressoDisponivel = ingresso.isIngressoDisponivel();
        this.vendido = ingresso.isVendido();
        this.disponivelParaCompra = !ingresso.isVendido();

        if (ingresso.getSessaoEvento() != null) {
            this.sessaoEventoId = ingresso.getSessaoEvento().getIdSessao();
        }

        if (ingresso.getTipoIngresso() != null) {
            this.idTipoIngresso = ingresso.getTipoIngresso().getIdTipoIngresso();
            this.nomeTipoIngresso = ingresso.getTipoIngresso().getNomeSetor();
        }
    }
}