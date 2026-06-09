package com.vendaingressos.dto.usuario;

import com.vendaingressos.model.Administrador;
import com.vendaingressos.model.Usuario;
import com.vendaingressos.model.enums.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class UsuarioPerfilResponse {

    private Long idUsuario;
    private String nome;
    private String cpf;
    private LocalDate dataNascimento;
    private String email;
    private String endereco;
    private String telefone;
    private Role role;

    public UsuarioPerfilResponse(Usuario usuario) {
        this.idUsuario = usuario.getIdUsuario();
        this.nome = usuario.getNome();
        this.cpf = usuario.getCpf();
        this.dataNascimento = usuario.getDataNascimento();
        this.email = usuario.getEmail();
        this.endereco = usuario.getEndereco();
        this.telefone = usuario.getTelefone();
        this.role = usuario.getRole();
    }

    public UsuarioPerfilResponse(Administrador administrador) {
        this.idUsuario = administrador.getIdAdmin();
        this.nome = administrador.getNome();
        this.email = administrador.getEmail();
        this.telefone = administrador.getTelefone();
        this.role = administrador.getRole();
    }
}
