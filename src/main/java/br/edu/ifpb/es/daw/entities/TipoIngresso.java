package br.edu.ifpb.es.daw.entities;

import jakarta.persistence.*;

import java.util.Objects;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tipo_ingresso")
public class TipoIngresso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_ingresso")
    private Long idTipoIngresso;
    @Column(name = "nome_setor")
    private String nomeSetor;
    private Double preco;
    @Column(name = "quantidade_total")
    private Integer quantidadeTotal;
    @Column(name = "quantidade_disponivel")
    private Integer quantidadeDisponivel;
    @Column(nullable = false)
    private Integer lote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sessao_id", nullable = false)
    private SessaoEvento sessao;

    @OneToOne(
            mappedBy = "tipoIngresso",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            optional = true
    )
    private Ingresso ingresso;

    public TipoIngresso() {}

    public TipoIngresso(String nomeSetor, Double preco, Integer quantidadeTotal, Integer quantidadeDisponivel, Integer lote) {
        this.nomeSetor = nomeSetor;
        this.preco = preco;
        this.quantidadeTotal = quantidadeTotal;
        this.quantidadeDisponivel = quantidadeDisponivel;
        this.lote = lote;
    }

    public Long getIdTipoIngresso() {
        return idTipoIngresso;
    }

    public void setIdTipoIngresso(Long idTipoIngresso) {
        this.idTipoIngresso = idTipoIngresso;
    }

    public String getNomeSetor() {
        return nomeSetor;
    }

    public void setNomeSetor(String nomeSetor) {
        this.nomeSetor = nomeSetor;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public Integer getQuantidadeTotal() {
        return quantidadeTotal;
    }

    public void setQuantidadeTotal(Integer quantidadeTotal) {
        this.quantidadeTotal = quantidadeTotal;
    }

    public Integer getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }

    public void setQuantidadeDisponivel(Integer quantidadeDisponivel) {
        this.quantidadeDisponivel = quantidadeDisponivel;
    }

    public Integer getLote() {
        return lote;
    }

    public void setLote(Integer lote) {
        this.lote = lote;
    }

    public SessaoEvento getSessao() { return sessao; }

    public void setSessao(SessaoEvento sessao) { this.sessao = sessao; }

    public Ingresso getIngresso() { return ingresso; }

    public void setIngresso(Ingresso ingresso) { this.ingresso = ingresso; }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TipoIngresso that = (TipoIngresso) o;
        return Objects.equals(idTipoIngresso, that.idTipoIngresso);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idTipoIngresso);
    }

    @Override
    public String toString() {
        return "TipoIngresso{" +
                "idTipoIngresso=" + idTipoIngresso +
                ", nomeSetor='" + nomeSetor + '\'' +
                ", preco=" + preco +
                ", lote=" + lote +
                ", sessaoEventoId=" + (sessao != null ? sessao.getIdSessao() : "null") +
                '}';
    }
}
