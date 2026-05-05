package com.vendaingressos.dto;

import com.vendaingressos.model.SessaoEvento;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SessaoEventoResponse {

    private Long idSessao;
    private String nomeSessao;
    private LocalDateTime dataHoraSessao;
    private String statusSessao;
    /** Null significa uso da capacidade do evento pai na compra. */
    private Integer capacidade;

    public SessaoEventoResponse(SessaoEvento sessaoEvento) {
        this.idSessao = sessaoEvento.getIdSessao();
        this.nomeSessao = sessaoEvento.getNomeSessao();
        this.dataHoraSessao = sessaoEvento.getDataHoraSessao();
        this.statusSessao = sessaoEvento.getStatusSessao();
        this.capacidade = sessaoEvento.getCapacidade();
    }
}
