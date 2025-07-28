package com.vendaingressos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "O ingresso deve estar associado a um evento")
    private Evento evento;

    @NotNull(message = "O preço do ingresso não pode ser nulo")
    @Min(value = 0, message = "O preço deve ser maior ou igual a zero")
    @Column(nullable = false)
    private double preco;

    @NotBlank(message = "O tipo de ingresso não pode estar em branco")
    @Column(nullable = false)
    private String tipoIngresso;

    private boolean ingressoDisponivel = true;

}
