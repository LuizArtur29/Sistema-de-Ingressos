package com.vendaingressos.model.mongo;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "avaliacaos_eventos")
@Data
public class AvaliacaoEvento {

    @Id
    private String id;

    private Long idEvento;
    private Long idUsuario;

    private String nomeUsuario;
    private int nota;
    private String comentario;
    private LocalDateTime dataAvaliacao;

    public AvaliacaoEvento(Long idEvento, Long idUsuario, String nomeUsuario, int nota, String comentario) {
        this.idEvento = idEvento;
        this.idUsuario = idUsuario;
        this.nomeUsuario = nomeUsuario;
        this.nota = nota;
        this.comentario = comentario;
        this.dataAvaliacao = LocalDateTime.now();
    }
}
