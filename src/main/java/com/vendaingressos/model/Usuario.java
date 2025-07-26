package com.vendaingressos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue
    private UUID idUsuario;

    private String nome;
    private int CPF;
    private String dataNascimento;
    private String email;
    private String senha;

    //Adicionar abaixo métoddos personalizados

}
