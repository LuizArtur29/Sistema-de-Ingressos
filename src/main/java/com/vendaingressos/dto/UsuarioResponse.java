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
        this.idUsuario = getIdUsuario();
        this.nome = getNome();
        this.cpf = getCpf();
        this.dataNascimento = getDataNascimento();
        this.email = getEmail();
        this.endereco = getEndereco();
        this.telefone = getTelefone();
    }
}
