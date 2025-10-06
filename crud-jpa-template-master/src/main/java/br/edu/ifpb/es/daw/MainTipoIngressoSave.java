package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.PersistenciaDawException;
import br.edu.ifpb.es.daw.dao.impl.TipoIngressoDAOImpl;
import br.edu.ifpb.es.daw.entities.TipoIngresso;

public class MainTipoIngressoSave {
    public static void main(String[] args) {
        TipoIngressoDAOImpl dao = new TipoIngressoDAOImpl();
        TipoIngresso tipoIngresso = new TipoIngresso(
                "Gestor do Evento Principal",
                150.0,
                1000,
                950,
                1
        );

        try {
            dao.save(tipoIngresso);
            System.out.println("Tipo de Ingresso salvo com sucesso! ID: " + tipoIngresso.getIdTipoIngresso());
        } catch (PersistenciaDawException e) {
            System.err.println("Erro ao salvar o tipo de ingresso:");
            e.printStackTrace();
        }
    }
}