package br.edu.ifpb.es.daw.entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "ingresso")
public class Ingresso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ingresso")
    private Long idIngresso;
    @Column(name = "ingresso_disponivel")
    private Boolean ingressoDisponivel = true;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_ingresso", unique = true,  nullable = false)
    private TipoIngresso tipoIngresso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_compra")  // quando vendido, aponta para a Compra
    private Compra compra;

    @OneToMany(mappedBy = "ingressoTransferido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Transferencia> transferencias = new HashSet<>();

    public Ingresso() {}
    public Ingresso(Boolean ingressoDisponivel) {
        this.ingressoDisponivel = ingressoDisponivel;
    }

    public Long getIdIngresso() {
        return idIngresso;
    }

    public void setIdIngresso(Long idIngresso) {
        this.idIngresso = idIngresso;
    }

    public Boolean isIngressoDisponivel() {
        return ingressoDisponivel;
    }

    public void setIngressoDisponivel(Boolean ingressoDisponivel) {
        this.ingressoDisponivel = ingressoDisponivel;
    }

    public TipoIngresso getTipoIngresso() { return tipoIngresso; }
    public void setTipoIngresso(TipoIngresso tipoIngresso) { this.tipoIngresso = tipoIngresso; }

    public Compra getCompra() { return compra; }
    public void setCompra(Compra compra) { this.compra = compra; }

    public Set<Transferencia> getTransferencias() { return this.transferencias; }
    public void setTransferencias(Set<Transferencia> transferencias) {
        this.transferencias = transferencias;
    }
    public void addTransferencia(Transferencia transferencia) {
        this.transferencias.add(transferencia);
        transferencia.setIngresso(this);
    }
    public void removeTransferencia(Transferencia transferencia) {
        this.transferencias.remove(transferencia);
        transferencia.setIngresso(null);
    }


    @Override
    public boolean equals(Object o) { /* ... */
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ingresso ingresso = (Ingresso) o;
        return Objects.equals(idIngresso, ingresso.idIngresso);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idIngresso);
    }

    @Override
    public String toString() { /* ... */
        return "Ingresso{" +
                "idIngresso=" + idIngresso +
                ", ingressoDisponivel=" + ingressoDisponivel +
                ", tipoIngressoId=" + (tipoIngresso != null ? tipoIngresso.getIdTipoIngresso() : "null") +
                ", compraId=" + (compra != null ? compra.getIdCompra() : "null") + // Adicionado ID da Compra
                '}';
    }
}