package com.vendaingressos.controller;

import com.vendaingressos.dto.IngressoResponse;
import com.vendaingressos.model.Ingresso;
import com.vendaingressos.service.IngressoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ingressos")
public class IngressoController {

    private final IngressoService ingressoService;

    @Autowired
    public IngressoController(IngressoService ingressoService) {
        this.ingressoService = ingressoService;
    }

    @PostMapping("/sessoes/{sessaoEventoId}/tipos/{tipoIngressoId}")
    public ResponseEntity<IngressoResponse> criarIngresso(
            @PathVariable Long sessaoEventoId,
            @PathVariable Long tipoIngressoId,
            @RequestBody Ingresso ingresso) {

        Ingresso novoIngresso = ingressoService.criarIngressoParaSessaoEvento(sessaoEventoId, tipoIngressoId, ingresso);

        return new ResponseEntity<>(new IngressoResponse(novoIngresso), HttpStatus.CREATED);
    }

    @GetMapping("/sessoes/{sessaoEventoId}")
    public ResponseEntity<List<IngressoResponse>> listarPorSessao(@PathVariable Long sessaoEventoId) {
        List<Ingresso> ingressos = ingressoService.listarIngressosPorSessaoEvento(sessaoEventoId);
        List<IngressoResponse> ingressosDTO = ingressos.stream()
                .map(IngressoResponse::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(ingressosDTO, HttpStatus.OK);
    }

    @GetMapping("/{ingressoId}")
    public ResponseEntity<IngressoResponse> buscarPorId(@PathVariable Long ingressoId) {
        return ingressoService.buscarIngressoPorId(ingressoId)
                .map(ingresso -> ResponseEntity.ok(new IngressoResponse(ingresso)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{ingressoId}/disponibilidade")
    public ResponseEntity<Boolean> isValido(@PathVariable Long ingressoId) {
        boolean valido = ingressoService.isIngressoValido(ingressoId);
        return new  ResponseEntity<>(valido, HttpStatus.OK);
    }

    @PutMapping("/{ingressoId}/entrada")
    public ResponseEntity<Void> registrarEntrada(@PathVariable Long ingressoId) {
        ingressoService.registrarEntrada(ingressoId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
