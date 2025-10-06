package br.edu.ifpb.es.daw.entities;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "transferencia")
public class Transferencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transferencia")
    private Long idTransferencia;
    @Column(name = "valor_revenda")
    private Double valorRevenda;

    public Transferencia() {
    }

    public Transferencia(Double valorRevenda) {
        this.valorRevenda = valorRevenda;
    }

    public Long getIdTransferencia() {
        return idTransferencia;
    }

    public void setIdTransferencia(Long idTransferencia) {
        this.idTransferencia = idTransferencia;
    }

    public Double getValorRevenda() {
        return valorRevenda;
    }

    public void setValorRevenda(Double valorRevenda) {
        this.valorRevenda = valorRevenda;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transferencia that = (Transferencia) o;
        return Objects.equals(idTransferencia, that.idTransferencia);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTransferencia);
    }

    @Override
    public String toString() {
        return "Transferencia{" +
                "idTransferencia=" + idTransferencia +
                ", valorRevenda=" + valorRevenda +
                '}';
    }
}
