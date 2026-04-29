package com.vendaingressos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UsuarioCreateRequest(

        @NotBlank(message = "O nome não pode estar em branco")
        String nome,

        @NotBlank(message = "O CPF não pode estar em branco")
        @Size(min = 11, max = 11, message = "O CPF deve conter 11 dígitos.")
        @Pattern(regexp = "\\d{11}", message = "O CPF deve conter apenas 11 dígitos numéricos.")
        @JsonProperty("CPF")
        String cpf,

        @NotNull(message = "A data de nascimento não pode ser nula")
        @Past
        LocalDate dataNascimento,

        @NotBlank(message = "O email não pode estar em branco")
        @Email(message = "Formato de email inválido")
        String email,

        @NotBlank(message = "A senha não pode estar em branco")
        String senha,

        @NotBlank(message = "O endereço não pode estar em branco")
        String endereco,

        @NotBlank(message = "O telefone não pode estar em branco")
        @Pattern(regexp = "^\\(?([0-9]{2})\\)?[-. ]?([0-9]{4,5})[-. ]?([0-9]{4})$",
                message = "Formato de telefone inválido. Use (XX) XXXX-XXXX ou (XX) XXXXX-XXXX")
        String telefone)
{
}
