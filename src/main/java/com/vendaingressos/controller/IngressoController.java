package com.vendaingressos.controller;

import com.vendaingressos.dto.IngressoResponse;
import com.vendaingressos.model.Ingresso;
import com.vendaingressos.service.IngressoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ingressos")
@CrossOrigin(origins = "http://localhost:8080")
public class IngressoController {

    private final IngressoService ingressoService;

    @Autowired
    public IngressoController(IngressoService ingressoService) {
        this.ingressoService = ingressoService;
    }

    // Alterado para criar ingresso para uma SessaoEvento específica
    @PostMapping("/criar/{sessaoEventoId}")
    public ResponseEntity<IngressoResponse> criarIngresso(@PathVariable Long sessaoEventoId, @Valid @RequestBody Ingresso ingresso) {
        Ingresso novoIngresso = ingressoService.criarIngressoParaSessaoEvento(sessaoEventoId, ingresso);
        return new ResponseEntity<>(new IngressoResponse(novoIngresso), HttpStatus.CREATED);
    }

    // Alterado para listar ingressos por SessaoEvento
    @GetMapping("listarIngressosPorSessao/{sessaoEventoId}")
    public ResponseEntity<List<IngressoResponse>> listarPorSessao(@PathVariable Long sessaoEventoId) {
        List<Ingresso> ingressos = ingressoService.listarIngressosPorSessaoEvento(sessaoEventoId);
        List<IngressoResponse> ingressosDTO = ingressos.stream()
                .map(IngressoResponse::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(ingressosDTO, HttpStatus.OK);
    }

    @GetMapping("buscarIngresso/{ingressoId}")
    public ResponseEntity<IngressoResponse> buscarPorId(@PathVariable Long ingressoId) {
        return ingressoService.buscarIngressoPorId(ingressoId)
                .map(ingresso -> ResponseEntity.ok(new IngressoResponse(ingresso)))
                .orElse(ResponseEntity.notFound().build());
    }

    /* Para ser usado dentro de outro método, para servir de verificação. O controller dessa lógica apenas para teste */
    @GetMapping("ingressoDisponivel/{ingressoId}")
    public ResponseEntity<Boolean> isValido(@PathVariable Long ingressoId) {
        boolean valido = ingressoService.isIngressoValido(ingressoId);
        return new  ResponseEntity<>(valido, HttpStatus.OK);
    }

    @PutMapping("registrarEntrada/{ingressoId}")
    public ResponseEntity<Void> registrarEntrada(@PathVariable Long ingressoId) {
        ingressoService.registrarEntrada(ingressoId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}