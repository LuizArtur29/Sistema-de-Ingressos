package com.vendaingressos.controller;

import com.vendaingressos.model.mongo.AvaliacaoEvento;
import com.vendaingressos.repository.mongo.AvaliacaoEventoRepository;
import com.vendaingressos.service.AvaliacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/avaliacoes")
@CrossOrigin(origins = "http://localhost:8080")
public class AvaliacaoController {

    @Autowired
    private AvaliacaoService avaliacaoService;

    @PostMapping
    public ResponseEntity<AvaliacaoEvento> criarAvaliacao(@RequestBody Map<String, Object> payload) {

        Long idEvento = Long.valueOf(payload.get("idEvento").toString());
        Long idUsuario = Long.valueOf(payload.get("idUsuario").toString());
        int nota = Integer.parseInt(payload.get("nota").toString());
        String comentario = (String) payload.get("comentario");

        AvaliacaoEvento novaAvaliacao = avaliacaoService.avaliarEvento(idEvento, idUsuario, nota, comentario);
        return ResponseEntity.ok(novaAvaliacao);
    }

    public ResponseEntity<List<AvaliacaoEvento>> listarPorEvento(@PathVariable Long idEvento) {
        return ResponseEntity.ok(avaliacaoService.listarAvaliacoesDoEvento(idEvento));
    }
}