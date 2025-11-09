package com.vendaingressos;

import com.vendaingressos.config.DatabaseConfig;
import com.vendaingressos.model.Compra;
import com.vendaingressos.model.Ingresso;
import com.vendaingressos.model.Usuario;
import com.vendaingressos.repository.jdbc.CompraRepositoryJDBC;
import com.vendaingressos.repository.jdbc.impl.CompraRepositoryJDBCImpl;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

/*public class TesteCompra {
    public static void main(String[] args) throws SQLException {
        Connection conn = DatabaseConfig.getConnection();
        CompraRepositoryJDBC compraRepo = new CompraRepositoryJDBCImpl(conn);

        Compra compra = new Compra();
        compra.setDataCompra(LocalDate.now());
        compra.setQuantidadeIngressos(2);
        compra.setValorTotal(200.0);
        compra.setMetodoPagamento("PIX");
        compra.setStatus("CONFIRMADA");

        // Supondo que usuario e ingresso já existam
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        Ingresso ingresso = new Ingresso();
        ingresso.setIdIngresso(1L);

        compra.setUsuario(usuario);
        compra.setIngresso(ingresso);

        compraRepo.salvar(compra);
    }
}
*/