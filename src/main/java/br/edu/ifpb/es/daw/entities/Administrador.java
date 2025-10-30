package br.edu.ifpb.es.daw.entities;

import jakarta.persistence.*;

import java.util.Objects;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "administrador")
public class Administrador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_admin")
    private Long idAdmin;
    private String nome;
    private String email;
    private String senha;
    private String telefone;

    @OneToMany(
            mappedBy = "administrador",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<Evento> eventosCriados = new ArrayList<>();

    public Administrador() {}

    public Administrador( String nome, String email, String senha, String telefone) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
    }

    public Long getIdAdmin() {
        return idAdmin;
    }

    public void setIdAdmin(Long idAdmin) {
        this.idAdmin = idAdmin;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getSenha() {
        return senha;
    }
    public void setSenha(String senha) {
        this.senha = senha;
    }
    public String getTelefone() {
        return telefone;
    }
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public List<Evento> getEventosCriados() {
        return eventosCriados;
    }
    public void setEventosCriados(List<Evento> eventosCriados) {
        this.eventosCriados = eventosCriados;
    }
    public void addEventoCriado(Evento e) {
        eventosCriados.add(e);
        e.setAdministrador(this);
    }
    public void removeEventoCriado(Evento e) {
        eventosCriados.remove(e);
        e.setAdministrador(null);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Administrador that = (Administrador) o;
        return Objects.equals(idAdmin, that.idAdmin);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idAdmin);
    }

    @Override
    public String toString() {
        return "Administrador{" +
                "idAdmin=" + idAdmin +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
