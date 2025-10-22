package br.edu.ifpb.es.daw.entities;


import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "sessoes_evento")
public class SessaoEvento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSessao;
    @Column(name = "nome_sessao")
    private String nomeSessao;
    @Column(name = "data_hora_sessao")
    private LocalDateTime dataHoraSessao;
    @Column(name = "status_sessao")
    private String statusSessao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id") // referencia PK de Evento (por padrão)
    private Evento evento;

    @OneToMany(
            mappedBy = "sessao",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<TipoIngresso> tipos = new ArrayList<>();

    public SessaoEvento() {
    }

    public SessaoEvento(String nomeSessao, LocalDateTime dataHoraSessao, String statusSessao) {
        this.nomeSessao = nomeSessao;
        this.dataHoraSessao = dataHoraSessao;
        this.statusSessao = statusSessao;
    }

    public void addTipo(TipoIngresso t) {
        tipos.add(t);
        t.setSessao(this);
    }

    public void removeTipo(TipoIngresso t) {
        tipos.remove(t);
        t.setSessao(null);
    }

    public Long getIdSessao() {
        return idSessao;
    }

    public void setIdSessao(Long idSessao) {
        this.idSessao = idSessao;
    }

    public String getNomeSessao() {
        return nomeSessao;
    }

    public void setNomeSessao(String nomeSessao) {
        this.nomeSessao = nomeSessao;
    }

    public LocalDateTime getDataHoraSessao() {
        return dataHoraSessao;
    }

    public void setDataHoraSessao(LocalDateTime dataHoraSessao) {
        this.dataHoraSessao = dataHoraSessao;
    }

    public String getStatusSessao() {
        return statusSessao;
    }

    public void setStatusSessao(String statusSessao) {
        this.statusSessao = statusSessao;
    }

    public Evento getEvento() { return evento; }

    public void setEvento(Evento evento) { this.evento = evento; }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SessaoEvento that = (SessaoEvento) o;
        return Objects.equals(idSessao, that.idSessao);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idSessao);
    }

    @Override
    public String toString() {
        return "SessaoEvento{" +
                "idSessao=" + idSessao +
                ", nomeSessao='" + nomeSessao + '\'' +
                ", dataHoraSessao=" + dataHoraSessao +
                ", statusSessao='" + statusSessao + '\'' +
                '}';
    }
}