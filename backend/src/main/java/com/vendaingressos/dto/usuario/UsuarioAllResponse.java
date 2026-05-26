package com.vendaingressos.dto.usuario;

import com.vendaingressos.model.Usuario;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UsuarioAllResponse {
    private Long idUsuario;
    private String nome;

    public UsuarioAllResponse(Usuario usuario) {
        this.idUsuario = usuario.getIdUsuario();
        this.nome = usuario.getNome();
    }
}
