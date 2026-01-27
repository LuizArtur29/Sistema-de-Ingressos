package com.vendaingressos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor; // Adicionado para construtor padrão
import lombok.AllArgsConstructor; // Adicionado para construtor com todos os campos

import java.time.LocalDate; // Alterado para LocalDate para data de início e fim
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "eventos")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do evento não pode estar em branco")
    @Column(nullable = false, length = 255)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false)
    @NotNull(message = "A data de início do evento não pode ser nula")
    @FutureOrPresent(message = "A data de início do evento não pode ser no passado")
    private LocalDate dataInicio; // Data de início do festival

    @Column(nullable = false)
    @NotNull(message = "A data de fim do evento não pode ser nula")
    @FutureOrPresent(message = "A data de fim do evento não pode ser no passado")
    private LocalDate dataFim; // Data de fim do festival

    @NotBlank(message = "O local do evento não pode estar em branco")
    @Column(nullable = false, length = 255)
    private String local;

    @NotNull(message = "A capacidade total do evento não pode ser nula")
    @Min(value = 1, message = "A capacidade total deve ser no mínimo 1")
    @Column(nullable = false)
    private Integer capacidadeTotal; // Capacidade por dia

    @NotBlank(message = "O status do evento não pode estar em branco")
    @Column(nullable = false)
    private String status; // Ex: "ATIVO", "CANCELADO", "FINALIZADO"

    // Relacionamento One-to-Many com SessaoEvento
    // CascadeType.ALL para operações em cascata (salvar, remover sessões junto com o evento)
    // orphanRemoval = true para remover sessões órfãs
    @OneToMany(mappedBy = "eventoPai", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SessaoEvento> sessoes = new ArrayList<>();


    public Evento() {
    }

    public Evento(Long id, String nome, String descricao, LocalDate dataInicio, LocalDate dataFim, String local, Integer capacidadeTotal, String status, List<SessaoEvento> sessoes) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.local = local;
        this.capacidadeTotal = capacidadeTotal;
        this.status = status;
        this.sessoes = sessoes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public Integer getCapacidadeTotal() {
        return capacidadeTotal;
    }

    public void setCapacidadeTotal(Integer capacidadeTotal) {
        this.capacidadeTotal = capacidadeTotal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<SessaoEvento> getSessoes() {
        return sessoes;
    }

    public void setSessoes(List<SessaoEvento> sessoes) {
        this.sessoes = sessoes;
    }
}