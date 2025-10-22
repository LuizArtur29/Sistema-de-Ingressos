package br.edu.ifpb.es.daw.entities;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "ingresso")

public class Ingresso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idIngresso;
    @Column(name = "ingresso_disponivel")
    private boolean ingressoDisponivel = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_ingresso_id")
    private TipoIngresso tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compra_id")  // quando vendido, aponta para a Compra
    private Compra compra;

    public Ingresso() {}
    public Ingresso(boolean ingressoDisponivel) {
        this.ingressoDisponivel = ingressoDisponivel;
    }

    public Long getIdIngresso() {
        return idIngresso;
    }

    public void setIdIngresso(Long idIngresso) {
        this.idIngresso = idIngresso;
    }

    public boolean isIngressoDisponivel() {
        return ingressoDisponivel;
    }

    public void setIngressoDisponivel(boolean ingressoDisponivel) {
        this.ingressoDisponivel = ingressoDisponivel;
    }

    public TipoIngresso getTipo() { return tipo; }
    public void setTipo(TipoIngresso tipo) { this.tipo = tipo; }

    public Compra getCompra() { return compra; }
    public void setCompra(Compra compra) { this.compra = compra; }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Ingresso ingresso = (Ingresso) o;
        return Objects.equals(idIngresso, ingresso.idIngresso);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idIngresso);
    }

    @Override
    public String toString() {
        return "Ingresso{" +
                "idIngresso=" + idIngresso +
                ", ingressoDisponivel=" + ingressoDisponivel +
                '}';
    }
}