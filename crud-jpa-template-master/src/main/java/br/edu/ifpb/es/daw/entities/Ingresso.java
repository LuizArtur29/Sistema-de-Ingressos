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