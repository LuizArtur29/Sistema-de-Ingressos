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
    private double preco;
    @Column(name = "quantidade_total")
    private int quantidadeTotal;
    @Column(name = "quantidade_disponivel")
    private int quantidadeDisponivel;
    private int lote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sessao_id")
    private SessaoEvento sessao;

    @OneToMany(
            mappedBy = "tipo",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<Ingresso> ingressos = new ArrayList<>();

    public TipoIngresso() {}

    public TipoIngresso(String nomeSetor, double preco, int quantidadeTotal, int quantidadeDisponivel, int lote) {
        this.nomeSetor = nomeSetor;
        this.preco = this.preco;
        this.quantidadeTotal = quantidadeTotal;
        this.quantidadeDisponivel = quantidadeDisponivel;
        this.lote = lote;
    }

    public void addIngresso(Ingresso i) {
        ingressos.add(i);
        i.setTipo(this);
    }

    public void removeIngresso(Ingresso i) {
        ingressos.remove(i);
        i.setTipo(null);
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

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public int getQuantidadeTotal() {
        return quantidadeTotal;
    }

    public void setQuantidadeTotal(int quantidadeTotal) {
        this.quantidadeTotal = quantidadeTotal;
    }

    public int getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }

    public void setQuantidadeDisponivel(int quantidadeDisponivel) {
        this.quantidadeDisponivel = quantidadeDisponivel;
    }

    public int getLote() {
        return lote;
    }

    public void setLote(int lote) {
        this.lote = lote;
    }

    public SessaoEvento getSessao() { return sessao; }

    public void setSessao(SessaoEvento sessao) { this.sessao = sessao; }

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
                '}';
    }
}
