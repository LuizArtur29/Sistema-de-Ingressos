package com.vendaingressos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


import java.time.LocalDate;

public record UsuarioUpdateRequest(
        String nome,

        @Past
        LocalDate dataNascimento,

        @Email(message = "Formato de email inválido")
        String email,

        @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
        String senha,

        String endereco,

        @Pattern(
                regexp = "^\\(?([0-9]{2})\\)?[-. ]?([0-9]{4,5})[-. ]?([0-9]{4})$",
                message = "Formato de telefone inválido. Use (XX) XXXX-XXXX ou (XX) XXXXX-XXXX"
        )
        String telefone
) {
}