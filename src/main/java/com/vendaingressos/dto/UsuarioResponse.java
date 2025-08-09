package com.vendaingressos.dto;

import com.vendaingressos.model.Usuario;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UsuarioResponse {

    private Long idUsuario;
    private String nome;
    private String cpf;
    private LocalDate dataNascimento;
    private String email;
    private String endereco;
    private String telefone;

    public UsuarioResponse(Usuario usuario) {
        this.idUsuario = usuario.getIdUsuario();
        this.nome = usuario.getNome();
        this.cpf = usuario.getCpf();
        this.dataNascimento = usuario.getDataNascimento();
        this.email = usuario.getEmail();
        this.endereco = usuario.getEndereco();
        this.telefone = usuario.getTelefone();
    }
}
