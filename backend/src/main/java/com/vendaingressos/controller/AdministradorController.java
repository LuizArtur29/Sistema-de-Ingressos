package com.vendaingressos.controller;

import com.vendaingressos.model.Administrador;
import com.vendaingressos.service.AdministradorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/administradores")
@CrossOrigin(origins = "http://localhost:8080")
public class AdministradorController {

    @Autowired
    private AdministradorService administradorService;

    @PostMapping
    public ResponseEntity<Administrador> criar(@RequestBody Administrador admin) {
        return new ResponseEntity<>(administradorService.cadastrar(admin), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Administrador>> listar() {
        return ResponseEntity.ok(administradorService.listarTodos());
    }
}