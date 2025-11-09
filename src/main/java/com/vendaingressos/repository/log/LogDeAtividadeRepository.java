package com.vendaingressos.repository.log;

import com.vendaingressos.model.log.LogDeAtividade;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogDeAtividadeRepository extends MongoRepository<LogDeAtividade, String> {

    List<LogDeAtividade> findByEmailUsuarioOrderByTimestampDesc(String emailUsuario);

    List<LogDeAtividade> findByAtividade(String atividade);
}
