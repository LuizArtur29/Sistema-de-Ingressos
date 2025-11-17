package com.vendaingressos.service;

import com.vendaingressos.model.mongo.LogDeAtividade;
import com.vendaingressos.repository.mongo.LogDeAtividadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LogService {

    private final LogDeAtividadeRepository logDeAtividadeRepository;

    @Autowired
    public LogService(LogDeAtividadeRepository logDeAtividadeRepository) {
        this.logDeAtividadeRepository = logDeAtividadeRepository;
    }

    public void registrarAtividade(String emailUsuario, String atividade, Map<String, Object> detalhes) {
        try {
            LogDeAtividade log = new LogDeAtividade(emailUsuario, atividade, detalhes);
            logDeAtividadeRepository.save(log);
        } catch (Exception e) {
            System.err.println("Falha ao salvar log no MongoDB: " + e.getMessage());
        }
    }
}
