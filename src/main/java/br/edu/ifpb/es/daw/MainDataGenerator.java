package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.DataGeneratorDAO;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class MainDataGenerator {

    public static void main(String[] args) {
        try(EntityManagerFactory emf = Persistence.createEntityManagerFactory("venda-ingressos-pu")) {
            DataGeneratorDAO dao = new DataGeneratorDAO(emf);
            dao.generateData();
        }
    }
}
