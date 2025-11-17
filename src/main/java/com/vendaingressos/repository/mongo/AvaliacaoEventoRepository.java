package com.vendaingressos.repository.mongo;

import com.vendaingressos.model.mongo.AvaliacaoEvento;
import com.vendaingressos.model.mongo.LogDeAtividade;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvaliacaoEventoRepository extends MongoRepository<AvaliacaoEvento, String> {
    List<AvaliacaoEvento> findByIdEvento(Long IdEvento);
}
