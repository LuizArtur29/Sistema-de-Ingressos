package com.vendaingressos.controller;

import com.vendaingressos.dto.TipoIngressoRequest;
import com.vendaingressos.model.SessaoEvento;
import com.vendaingressos.model.TipoIngresso;
import com.vendaingressos.repository.SessaoEventoRepository;
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

    @Autowired
    private SessaoEventoRepository sessaoEventoRepository;

    @PostMapping
    public ResponseEntity<TipoIngresso> criar(@RequestBody TipoIngressoRequest request) {
        TipoIngresso tipo = new TipoIngresso();
        tipo.setNomeSetor(request.getNomeSetor());
        tipo.setPreco(request.getPreco());
        tipo.setQuantidadeTotal(request.getQuantidadeTotal());
        tipo.setQuantidadeDisponivel(request.getQuantidadeTotal());
        tipo.setLote(request.getLote());

        SessaoEvento sessao = sessaoEventoRepository.findById(request.getSessaoId())
                .orElseThrow(() -> new RuntimeException("Sessão não encontrada"));

        tipo.setSessao(sessao);

        return ResponseEntity.ok(tipoIngressoService.salvar(tipo));
    }

    @GetMapping("/sessao/{sessaoId}")
    public ResponseEntity<List<TipoIngresso>> listar(@PathVariable Long sessaoId) {
        return ResponseEntity.ok(tipoIngressoService.listarPorSessao(sessaoId));
    }
}