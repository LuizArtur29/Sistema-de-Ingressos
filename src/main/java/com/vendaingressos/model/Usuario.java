package com.vendaingressos.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    @NotBlank(message = "O nome não pode estar em branco")
    private String nome;

    @NotBlank(message = "O CPF não pode estar em branco")
    @Size(min = 11, max = 11, message = "O CPF deve conter 11 dígitos.")
    @Pattern(regexp = "\\d{11}", message = "O CPF deve conter apenas 11 dígitos numéricos.")
    private String cpf;

    @NotNull(message = "A data de nascimento não pode ser nula")
    private LocalDate dataNascimento;

    @NotBlank(message = "O email não pode estar em branco")
    @Email(message = "Formato de email inválido")
    private String email;

    @NotBlank(message = "A senha não pode estar em branco")
    private String senha;

    @NotBlank(message = "O endereço não pode estar em branco")
    private String endereco;

    @NotBlank(message = "O telefone não pode estar em branco")
    @Pattern(regexp = "^\\(?([0-9]{2})\\)?[-. ]?([0-9]{4,5})[-. ]?([0-9]{4})$",
            message = "Formato de telefone inválido. Use (XX) XXXX-XXXX ou (XX) XXXXX-XXXX")
    private String telefone;

    //Adicionar abaixo métoddos personalizados
}