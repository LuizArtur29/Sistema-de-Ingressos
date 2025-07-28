package com.vendaingressos.controller;

import com.vendaingressos.model.Ingresso;
import com.vendaingressos.service.IngressoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ingressos")
@CrossOrigin(origins = "http://localhost:8080")
public class IngressoController {

    private final IngressoService ingressoService;

    @Autowired
    public IngressoController(IngressoService ingressoService) {
        this.ingressoService = ingressoService;
    }

    @PostMapping("/criar/{eventoId}")
    public ResponseEntity<Ingresso> criarIngresso(@PathVariable Long eventoId, @RequestBody Ingresso ingresso) {
        Ingresso novoIngresso = ingressoService.criarIngressoParaEvento(eventoId, ingresso);
        return new ResponseEntity<>(novoIngresso, HttpStatus.CREATED);
    }

    @GetMapping("listarIngressos/{eventoId}")
    public ResponseEntity<List<Ingresso>> listarPorEvento(@PathVariable Long eventoId) {
        List<Ingresso> ingressos = ingressoService.listarIngressosPorEvento(eventoId);
        return new ResponseEntity<>(ingressos, HttpStatus.OK);
    }

    @GetMapping("buscarIngresso/{ingressoId}")
    public ResponseEntity<Ingresso> buscarPorId(@PathVariable Long ingressoId) {
        return ingressoService.buscarIngressoPorId(ingressoId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    /* Para ser usado dentro de outro método, para servir de verificação. O controller dessa lógica apenas para teste */
    @GetMapping("ingressoDisponivel/{ingressoId}")
    public ResponseEntity<Boolean> isValido(@PathVariable Long ingressoId) {
        boolean valido = ingressoService.isIngressoValido(ingressoId);
        return new  ResponseEntity<>(valido, HttpStatus.OK);
    }

    @PutMapping("registrarEntrada/{ingressoId}")
    public ResponseEntity<Void> registrarEntrada(Long ingressoId) {
        ingressoService.registrarEntrada(ingressoId);
        return new ResponseEntity<>(HttpStatus.OK);

    }

}
