package com.vendaingressos.dto;

import com.vendaingressos.model.Compra;
import com.vendaingressos.model.Ingresso;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
    /** Primeiro ID (menor), compatível com respostas antigas; prefira {@link #ingressoIds}. */
    private Long ingressoId;
    /** Todos os ingressos ligados à compra (um por unidade). */
    private List<Long> ingressoIds = List.of();
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

        List<Ingresso> lista = compra.getIngressos() != null
                ? new ArrayList<>(compra.getIngressos())
                : new ArrayList<>();
        lista.sort(Comparator.comparing(Ingresso::getIdIngresso));
        if (!lista.isEmpty()) {
            Ingresso ref = lista.get(0);
            this.ingressoId = ref.getIdIngresso();
            this.nomeEvento = ref.getSessaoEvento().getEventoPai().getNome();
            this.ingressoIds = lista.stream().map(Ingresso::getIdIngresso).toList();
        }
    }
}
