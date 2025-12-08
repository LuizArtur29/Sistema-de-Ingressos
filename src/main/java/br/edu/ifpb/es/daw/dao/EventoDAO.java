package br.edu.ifpb.es.daw.dao;

import br.edu.ifpb.es.daw.entities.Evento;

public interface EventoDAO extends DAO<Evento, Long> {

    Evento findEventByNameAndLocalEvent(String nomeEvento, String local) throws PersistenciaDawException;

    Long getTheCountOfAllEvents() throws PersistenciaDawException;

    Evento findEventByNameWithAllSsections(String nomeEvento) throws PersistenciaDawException;

}
