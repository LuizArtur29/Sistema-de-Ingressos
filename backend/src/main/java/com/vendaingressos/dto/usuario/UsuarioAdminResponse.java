package com.vendaingressos.dto.usuario;

import com.vendaingressos.model.Usuario;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class UsuarioAdminResponse {

    private Long idUsuario;
    private String nome;
    private LocalDate dataNascimento;
    private String email;
    private String endereco;
    private String telefone;

    public UsuarioAdminResponse(Usuario usuario) {
        this.idUsuario = usuario.getIdUsuario();
        this.nome = usuario.getNome();
        this.dataNascimento = usuario.getDataNascimento();
        this.email = usuario.getEmail();
        this.endereco = usuario.getEndereco();
        this.telefone = usuario.getTelefone();
    }

}
