package br.edu.ifpb.es.daw.entities;


import br.edu.ifpb.es.daw.entities.enums.Status;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;


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
    private Status statusSessao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id", nullable = false) // Coluna FK na tabela 'sessoes_evento'
    private Evento eventoPai;

    @OneToMany(
            mappedBy = "sessao",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private Set<TipoIngresso> tiposIngresso = new HashSet<>();

    public SessaoEvento() {
    }

    public SessaoEvento(String nomeSessao, LocalDateTime dataHoraSessao, Status statusSessao) {
        this.nomeSessao = nomeSessao;
        this.dataHoraSessao = dataHoraSessao;
        this.statusSessao = statusSessao;
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

    public Status getStatusSessao() {
        return statusSessao;
    }

    public void setStatusSessao(Status statusSessao) {
        this.statusSessao = statusSessao;
    }

    public Evento getEventoPai() { return eventoPai; }

    public void setEventoPai(Evento eventoPai) { this.eventoPai = eventoPai; }

    public Set<TipoIngresso> getTiposIngresso() {
        return tiposIngresso;
    }
    public void setTiposIngresso(Set<TipoIngresso> tiposIngresso) { this.tiposIngresso = tiposIngresso; }
    public void addTipoIngresso(TipoIngresso tipo) {
        tiposIngresso.add(tipo);
        tipo.setSessao(this);
    }

    public void removeTipoIngresso(TipoIngresso tipo) {
        tiposIngresso.remove(tipo);
        tipo.setSessao(null);
    }

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