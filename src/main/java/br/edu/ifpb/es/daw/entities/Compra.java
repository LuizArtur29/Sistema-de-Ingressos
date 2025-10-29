package br.edu.ifpb.es.daw.entities;

import br.edu.ifpb.es.daw.entities.enums.Status;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import br.edu.ifpb.es.daw.entities.enums.MetodoPagamento;

@Entity
@Table(name = "compra")
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_compra")
    private Long idCompra;

    @Column(name = "data_compra", nullable = false)
    private LocalDateTime dataCompra;

    @Column(name = "quantidade_ingressos", nullable = false)
    private Integer quantidadeIngressos;

    @Column(name = "valor_total", nullable = false, precision = 19, scale = 2)
    private BigDecimal valorTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pagamento", nullable = false, length = 30)
    private MetodoPagamento metodoPagamento;

    @Column(name = "status", length = 30, nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(
            mappedBy = "compra",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private Set<Ingresso> ingressos = new HashSet<>();

    public Compra() {}

    public Compra(LocalDateTime dataCompra,
                  Integer quantidadeIngressos,
                  BigDecimal valorTotal,
                  MetodoPagamento metodoPagamento,
                  Status status) {
        this.dataCompra = dataCompra;
        this.quantidadeIngressos = quantidadeIngressos;
        this.valorTotal = valorTotal;
        this.metodoPagamento = metodoPagamento;
        this.status = status;
    }

    @PrePersist
    protected void onCreate() {
        if (this.dataCompra == null) {
            this.dataCompra = LocalDateTime.now();
        }
    }

    public Long getIdCompra() { return idCompra; }

    public void setIdCompra(Long idCompra) { this.idCompra = idCompra; }
    public LocalDateTime getDataCompra() { return dataCompra; }
    public void setDataCompra(LocalDateTime dataCompra) { this.dataCompra = dataCompra; }
    public int getQuantidadeIngressos() { return quantidadeIngressos; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
    public MetodoPagamento getMetodoPagamento() { return metodoPagamento; }
    public void setMetodoPagamento(MetodoPagamento metodoPagamento) { this.metodoPagamento = metodoPagamento;}
    public Status getStatus() { return status; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Set<Ingresso> getIngressos() { return ingressos; }
    public void setIngressos(Set<Ingresso> ingressos) {
        this.ingressos = ingressos;
    }
    public void addIngresso(Ingresso ingresso) {
        this.ingressos.add(ingresso);
        ingresso.setCompra(this);
        this.quantidadeIngressos = this.ingressos.size();
        }

    public void removeIngresso(Ingresso ingresso) {
        this.ingressos.remove(ingresso);
        ingresso.setCompra(null);
        this.quantidadeIngressos = this.ingressos.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;                 // identidade
        if (o == null || getClass() != o.getClass()) return false;
        Compra other = (Compra) o;
        if (idCompra == null || other.idCompra == null) return false;  // entidades novas ≠
        return idCompra.equals(other.idCompra);
    }

    @Override
    public int hashCode() {
        return (idCompra == null) ? 0 : idCompra.hashCode();
    }

    @Override
    public String toString() {
        return "Compra{" +
                "idCompra=" + idCompra +
                ", dataCompra=" + dataCompra +
                ", valorTotal=" + valorTotal +
                ", metodoPagamento=" + metodoPagamento +
                ", status='" + status + '\'' +
                ", usuarioId=" + (usuario != null ? usuario.getIdUsuario() : "null") +
                ", quantidadeIngressos=" + quantidadeIngressos +
                '}';
    }
}
