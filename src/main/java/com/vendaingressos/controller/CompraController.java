package com.vendaingressos.controller;


import com.vendaingressos.dto.CompraRequest;
import com.vendaingressos.dto.StatusUpdateRequest;
import com.vendaingressos.exception.ResourceNotFoundException;
import com.vendaingressos.model.Compra;
import com.vendaingressos.service.CompraService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<Compra> realizarCompra(@Valid @RequestBody CompraRequest request) {
            Compra novaCompra = compraService.realizarCompra(
                    request.getUsuarioID(),
                    request.getIngressoID(),
                    request.getQuantidadeIngressos(),
                    request.getMetodoPagamento(),
                    request.isMeiaEntrada
                    );
            return new ResponseEntity<>(novaCompra, HttpStatus.CREATED);
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
                .orElseThrow(() -> new ResourceNotFoundException("Compra não encontrada com ID: " + id));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Compra>> buscarComprasPorUsuario(@PathVariable Long usuarioId) {
        List<Compra> compras = compraService.buscarComprasPorUsuario(usuarioId);
        return new ResponseEntity<>(compras, HttpStatus.OK);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Compra> atualizarStatusCompra(@PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
            Compra compraAtualizada = compraService.atualizarStatusCompra(id, request.novoStatus);
            return new ResponseEntity<>(compraAtualizada, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCompra(@PathVariable Long id) {
        compraService.deletarCompra(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}