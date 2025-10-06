package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.PersistenciaDawException;
import br.edu.ifpb.es.daw.dao.impl.TransferenciaDAOImpl;
import br.edu.ifpb.es.daw.entities.Transferencia;

public class MainTransferenciaSave {
    public static void main(String[] args) {
        TransferenciaDAOImpl dao = new TransferenciaDAOImpl();
        Transferencia transferencia = new Transferencia(200.00);

        try {
            dao.save(transferencia);
            System.out.println("Transferência salva com sucesso! ID: " + transferencia.getIdTransferencia());
        } catch (PersistenciaDawException e) {
            System.err.println("Erro ao salvar a transferência:");
            e.printStackTrace();
        }
    }
}
