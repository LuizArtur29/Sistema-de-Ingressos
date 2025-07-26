package com.vendaingressos.model;

import jakarta.persistence.*;
import lombok.*;

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

    private String nome;
    private int CPF;
    private String dataNascimento;
    private String email;
    private String senha;

    //Adicionar abaixo métoddos personalizados

}
