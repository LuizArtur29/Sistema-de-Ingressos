package com.vendaingressos.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "compra")
public class Compra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCompra;

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

    public Compra() {
    }

    public Compra(Long idCompra, LocalDate dataCompra, int quantidadeIngressos, double valorTotal, String metodoPagamento, String status, Usuario usuario, Ingresso ingresso) {
        this.idCompra = idCompra;
        this.dataCompra = dataCompra;
        this.quantidadeIngressos = quantidadeIngressos;
        this.valorTotal = valorTotal;
        this.metodoPagamento = metodoPagamento;
        this.status = status;
        this.usuario = usuario;
        this.ingresso = ingresso;
    }

    public Long getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(Long idCompra) {
        this.idCompra = idCompra;
    }

    public LocalDate getDataCompra() {
        return dataCompra;
    }

    public void setDataCompra(LocalDate dataCompra) {
        this.dataCompra = dataCompra;
    }

    public int getQuantidadeIngressos() {
        return quantidadeIngressos;
    }

    public void setQuantidadeIngressos(int quantidadeIngressos) {
        this.quantidadeIngressos = quantidadeIngressos;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(String metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Ingresso getIngresso() {
        return ingresso;
    }

    public void setIngresso(Ingresso ingresso) {
        this.ingresso = ingresso;
    }
}
