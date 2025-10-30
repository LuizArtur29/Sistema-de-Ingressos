package br.edu.ifpb.es.daw.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;
    private String nome;
    private String cpf;
    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;
    private String email;
    private String senha;
    private String endereco;
    private String telefone;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Compra> compras = new ArrayList<>();

    @OneToMany(mappedBy = "vendedor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Transferencia> transferenciasVendidas = new ArrayList<>();

    @OneToMany(mappedBy = "comprador", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Transferencia> transferenciasCompradas = new ArrayList<>();

    public Usuario() {
    }

    public Usuario(String nome, String cpf, LocalDate dataNascimento, String email, String senha, String endereco, String telefone) {
        this.nome = nome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.email = email;
        this.senha = senha;
        this.endereco = endereco;
        this.telefone = telefone;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
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

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public List<Compra> getCompras() {
        return compras;
    }
    public void setCompras(List<Compra> compras) {
        this.compras = compras;
    }
    public void addCompra(Compra c) {
        compras.add(c);
        c.setUsuario(this);
    }
    public void removeCompra(Compra c) {
        compras.remove(c);
        c.setUsuario(null);
    }

    public List<Transferencia> getTransferenciasVendidas() {
        return transferenciasVendidas;
    }
    public void setTransferenciasVendidas(List<Transferencia> transferenciasVendidas) {
        this.transferenciasVendidas = transferenciasVendidas;
    }
    public void addTransferenciaVendida(Transferencia t) {
        transferenciasVendidas.add(t);
        t.setVendedor(this);
    }
    public void removeTransferenciaVendida(Transferencia t) {
        transferenciasVendidas.remove(t);
        t.setVendedor(null);
    }
    public List<Transferencia> getTransferenciasCompradas() {
        return transferenciasCompradas;
    }

    public void setTransferenciasCompradas(List<Transferencia> transferenciasCompradas) {
        this.transferenciasCompradas = transferenciasCompradas;
    }
    public void addTransferenciaComprada(Transferencia t) {
        transferenciasCompradas.add(t);
        t.setVendedor(this);
    }
    public void removeTransferenciaComprada(Transferencia t) {
        transferenciasCompradas.remove(t);
        t.setVendedor(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(idUsuario, usuario.idUsuario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}