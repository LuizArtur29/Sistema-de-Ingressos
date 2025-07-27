package com.vendaingressos.controller;

<<<<<<< Updated upstream
public class CompraController {
}
=======
import com.vendaingressos.dto.CompraRequest;
import com.vendaingressos.dto.StatusUpdateRequest;
import com.vendaingressos.model.Compra;
import com.vendaingressos.service.CompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/compras")
@CrossOrigin(origins = "http://localhost:8080")
public class CompraController {

    private final CompraService compraService;

    @Autowired
    public CompraController(CompraService compraService) {
        this.compraService = compraService;
    }

    @PostMapping
    public ResponseEntity<Compra> realizarCompra(@RequestBody CompraRequest request) {

        try {
            Compra novaCompra = compraService.realizarCompra(
                    request.usuarioId,
                    request.ingressoId,
                    request.quantidadeIngressos,
                    request.isMeiaEntrada
                    );
            return new ResponseEntity<>(novaCompra, HttpStatus.CREATED);
        }catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<Compra>> listarTodasCompras() {
        List<Compra> compras = compraService.buscarTodasCompras();
        return new ResponseEntity<>(compras, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Compra> buscarCompraPorId(@PathVariable Long id) {
        return compraService.buscarCompraPorId(id)
                .map(compra -> new ResponseEntity<>(compra, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Compra>> buscarComprasPorUsuario(@PathVariable Long usuarioId) {
        List<Compra> compras = compraService.buscarComprasPorUsuario(usuarioId);
        if (compras.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(compras, HttpStatus.OK);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Compra> atualizarStatusCompra(@PathVariable Long id, @RequestBody StatusUpdateRequest request) {
        try {
            Compra compraAtualizada = compraService.atualizarStatusCompra(id, request.novoStatus);
            return new ResponseEntity<>(compraAtualizada, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCompra(@PathVariable Long id) {
        compraService.deletarCompra(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
>>>>>>> Stashed changes
