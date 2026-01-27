package com.vendaingressos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "ingresso")
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_ingresso")
    private TipoIngresso tipoIngresso;

    private boolean ingressoDisponivel = true;

    public Ingresso() {
    }

    public Ingresso(Long idIngresso, SessaoEvento sessaoEvento, double preco, TipoIngresso tipoIngresso, boolean ingressoDisponivel) {
        this.idIngresso = idIngresso;
        this.sessaoEvento = sessaoEvento;
        this.preco = preco;
        this.tipoIngresso = tipoIngresso;
        this.ingressoDisponivel = ingressoDisponivel;
    }

    public Long getIdIngresso() {
        return idIngresso;
    }

    public void setIdIngresso(Long idIngresso) {
        this.idIngresso = idIngresso;
    }

    public SessaoEvento getSessaoEvento() {
        return sessaoEvento;
    }

    public void setSessaoEvento(SessaoEvento sessaoEvento) {
        this.sessaoEvento = sessaoEvento;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public TipoIngresso getTipoIngresso() {
        return tipoIngresso;
    }

    public void setTipoIngresso(TipoIngresso tipoIngresso) {
        this.tipoIngresso = tipoIngresso;
    }

    public boolean isIngressoDisponivel() {
        return ingressoDisponivel;
    }

    public void setIngressoDisponivel(boolean ingressoDisponivel) {
        this.ingressoDisponivel = ingressoDisponivel;
    }
}