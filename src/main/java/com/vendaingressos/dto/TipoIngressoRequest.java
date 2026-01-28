package com.vendaingressos.dto;

import lombok.Data;

@Data
public class TipoIngressoRequest {
    private String nomeSetor;
    private Double preco;
    private Integer quantidadeTotal;
    private Integer lote;
    private Long sessaoId;
}