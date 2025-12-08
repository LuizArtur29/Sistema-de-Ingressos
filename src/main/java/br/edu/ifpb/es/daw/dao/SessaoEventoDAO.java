package br.edu.ifpb.es.daw.dao;

import br.edu.ifpb.es.daw.entities.Evento;
import br.edu.ifpb.es.daw.entities.SessaoEvento;

public interface SessaoEventoDAO extends DAO<SessaoEvento, Long> {

    SessaoEvento findSectionEventByEventObject(Evento eventoPai) throws PersistenciaDawException;

}