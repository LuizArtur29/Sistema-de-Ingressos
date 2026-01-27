package com.vendaingressos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "sessoes_evento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessaoEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSessao;

    @NotBlank(message = "O nome da sessão não pode estar em branco")
    @Column(nullable = false, length = 255)
    private String nomeSessao; // Ex: "Dia 1", "Sábado", "Sessão da Manhã"

    @Column(nullable = false)
    @NotNull(message = "A data e hora da sessão não pode ser nula")
    @FutureOrPresent(message = "A data e hora da sessão não pode ser no passado")
    private LocalDateTime dataHoraSessao; // Data e hora específica da sessão/dia

    @NotBlank(message = "O status da sessão não pode estar em branco")
    @Column(nullable = false)
    private String statusSessao; // Ex: "ATIVO", "ESGOTADO", "CANCELADO"

    @ManyToOne(fetch = FetchType.LAZY) // Many sessions to one Event
    @JoinColumn(name = "id_evento", nullable = false) // Foreign key to Evento
    @NotNull(message = "A sessão deve estar associada a um evento pai")
    private Evento eventoPai;

    // Construtor com campos necessários para criação rápida (excluindo ID e Evento)
    public SessaoEvento(String nomeSessao, LocalDateTime dataHoraSessao, String statusSessao, Evento eventoPai) {
        this.nomeSessao = nomeSessao;
        this.dataHoraSessao = dataHoraSessao;
        this.statusSessao = statusSessao;
        this.eventoPai = eventoPai;
    }
}