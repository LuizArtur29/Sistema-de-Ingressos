package com.vendaingressos.dto;

import com.vendaingressos.model.Compra;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CompraResponse {

    private Long idCompra;
    private LocalDate dataCompra;
    private int quantidadeIngressos;
    private double valorTotal;
    private String metodoPagamento;
    private String status;
    private Long usuarioId;
    private String nomeUsuario;
    private Long ingressoId;
    private String nomeEvento;

    public CompraResponse(Compra compra) {
        this.idCompra = compra.getIdCompra();
        this.dataCompra = compra.getDataCompra();
        this.quantidadeIngressos = compra.getQuantidadeIngressos();
        this.valorTotal = compra.getValorTotal();
        this.metodoPagamento = compra.getMetodoPagamento();
        this.status = compra.getStatus();
        this.usuarioId = compra.getUsuario().getIdUsuario();
        this.nomeUsuario = compra.getUsuario().getNome();
        this.ingressoId = compra.getIngresso().getIdIngresso();
        this.nomeEvento = compra.getIngresso().getSessaoEvento().getEventoPai().getNome();
    }
}