package com.vendaingressos.model;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import com.vendaingressos.model.enums.Role;
import lombok.*;

import java.util.Objects;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "administrador")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Administrador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_admin")
    private Long idAdmin;
    private String nome;
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String senha;

    private String telefone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.ADMINISTRADOR;

    @OneToMany(
            mappedBy = "administrador",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<Evento> eventosCriados = new ArrayList<>();
}