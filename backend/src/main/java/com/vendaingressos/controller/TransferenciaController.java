package com.vendaingressos.controller;

import com.vendaingressos.model.Transferencia;
import com.vendaingressos.service.TransferenciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transferencias")
@CrossOrigin(origins = "http://localhost:8080")
public class TransferenciaController {

    @Autowired
    private TransferenciaService transferenciaService;

    @PostMapping
    public ResponseEntity<Transferencia> transferir(@RequestBody Transferencia transferencia) {
        return ResponseEntity.ok(transferenciaService.realizarTransferencia(transferencia));
    }
}