package com.vendaingressos.model.mongo;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalTime;
import java.util.Map;

@Document(collection = "logs_de_atividade")
@Data
public class LogDeAtividade {

    @Id
    private String id;

    private LocalTime timestamp;

    private String emailUsuario;
    private String atividade;

    private Map<String, Object> detalhes;

    public LogDeAtividade(String emailUsuario, String atividade, Map<String, Object> detalhes) {
        this.emailUsuario = emailUsuario;
        this.atividade = atividade;
        this.detalhes = detalhes;
    }

}