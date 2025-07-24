package com.vendaingressos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "compra")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Compra {
    @Id
    @GeneratedValue
    private UUID idCompra;

    private LocalDate dataCompra;
    private int quantidadeIngressos;
    private double valorTotal;
    private String metodoPagamento;
    private String status;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_ingresso")
    private Ingresso ingresso;

}
