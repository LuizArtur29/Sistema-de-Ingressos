package com.vendaingressos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "ingresso")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ingresso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idIngresso;

    // Alterado de Evento para SessaoEvento
    @ManyToOne(fetch = FetchType.LAZY) // Many tickets to one session
    @JoinColumn(name = "id_sessao_evento", nullable = false) // Foreign key to SessaoEvento
    @NotNull(message = "O ingresso deve estar associado a uma sessão de evento")
    private SessaoEvento sessaoEvento;

    @NotNull(message = "O preço do ingresso não pode ser nulo")
    @Min(value = 0, message = "O preço deve ser maior ou igual a zero")
    @Column(nullable = false)
    private double preco;

    @NotBlank(message = "O tipo de ingresso não pode estar em branco")
    @Column(nullable = false)
    private String tipoIngresso;

    private boolean ingressoDisponivel = true;
}