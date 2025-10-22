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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingresso_id")
    private Ingresso ingresso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendedor_id")
    private Usuario vendedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comprador_id")
    private Usuario comprador;

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

    public Ingresso getIngresso() { return ingresso; }
    public void setIngresso(Ingresso ingresso) { this.ingresso = ingresso; }

    public Usuario getVendedor() { return vendedor; }
    public void setVendedor(Usuario vendedor) { this.vendedor = vendedor; }

    public Usuario getComprador() { return comprador; }
    public void setComprador(Usuario comprador) { this.comprador = comprador; }

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
