package com.vendaingressos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "ingresso")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Ingresso {
    @Id
    @GeneratedValue
    private UUID idIngresso;

    @ManyToOne
    @JoinColumn(name = "id_evento")
    private Evento evento;

}
