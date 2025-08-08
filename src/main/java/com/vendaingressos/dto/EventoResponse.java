package com.vendaingressos.dto;

import com.vendaingressos.model.Evento;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class EventoResponse {

    private Long id;
    private String nome;
    private String descricao;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String local;
    private Integer capacidadeTotal;
    private String status;
    private List<SessaoEventoResponse> sessoes;

    public EventoResponse(Evento evento) {
        this.id = evento.getId();
        this.nome = evento.getNome();
        this.descricao = evento.getDescricao();
        this.dataInicio = evento.getDataInicio();
        this.dataFim = evento.getDataFim();
        this.local = evento.getLocal();
        this.capacidadeTotal = evento.getCapacidadeTotal();
        this.status = evento.getStatus();
        // Converte a lista de entidades SessaoEvento para uma lista de DTOs
        this.sessoes = evento.getSessoes().stream()
                .map(SessaoEventoResponse::new)
                .collect(Collectors.toList());
    }
}
