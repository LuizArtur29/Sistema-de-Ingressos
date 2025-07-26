package com.vendaingressos.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "ingresso")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ingresso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idIngresso;

    @ManyToOne
    @JoinColumn(name = "id_evento")
    private Evento evento;

    @Column(nullable = false)
    private double preco;

    @Column(nullable = false)
    private String tipoIngresso;

    private boolean ingressoDisponivel = true;

}
