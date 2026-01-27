package com.vendaingressos.controller;

import com.vendaingressos.model.TipoIngresso;
import com.vendaingressos.service.TipoIngressoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-ingresso")
@CrossOrigin(origins = "http://localhost:8080")
public class TipoIngressoController {

    @Autowired
    private TipoIngressoService tipoIngressoService;

    @PostMapping
    public ResponseEntity<TipoIngresso> criar(@RequestBody TipoIngresso tipo) {
        return ResponseEntity.ok(tipoIngressoService.salvar(tipo));
    }

    @GetMapping("/sessao/{sessaoId}")
    public ResponseEntity<List<TipoIngresso>> listar(@PathVariable Long sessaoId) {
        return ResponseEntity.ok(tipoIngressoService.listarPorSessao(sessaoId));
    }
}