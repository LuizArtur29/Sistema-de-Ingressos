package com.vendaingressos.repository;

import com.vendaingressos.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

    List<Evento> findByAdministradorEmail(String email);

    @Query("SELECT DISTINCT e FROM Evento e JOIN e.sessoes s WHERE s.idSessao IN "
            + "(SELECT i.sessaoEvento.idSessao FROM Ingresso i WHERE i.compra IS NOT NULL AND i.compra.usuario.email = :email)")
    List<Evento> findEventsPurchasedByUserEmail(@Param("email") String email);
}
