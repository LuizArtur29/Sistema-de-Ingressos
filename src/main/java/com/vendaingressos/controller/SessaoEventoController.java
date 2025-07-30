package com.vendaingressos.controller;

import com.vendaingressos.exception.ResourceNotFoundException;
import com.vendaingressos.model.SessaoEvento;
import com.vendaingressos.service.SessaoEventoService; // Você precisará criar este serviço também
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessoes-evento") // Define o caminho base para os endpoints de sessão de evento
@CrossOrigin(origins = "http://localhost:8080") // Permite requisições do frontend
public class SessaoEventoController {

    private final SessaoEventoService sessaoEventoService;

    @Autowired
    public SessaoEventoController(SessaoEventoService sessaoEventoService) {
        this.sessaoEventoService = sessaoEventoService;
    }

    // Endpoint para criar uma nova sessão de evento (POST /api/sessoes-evento)
    @PostMapping
    public ResponseEntity<SessaoEvento> criarSessaoEvento(@Valid @RequestBody SessaoEvento sessaoEvento) {
        SessaoEvento novaSessao = sessaoEventoService.salvarSessaoEvento(sessaoEvento);
        return new ResponseEntity<>(novaSessao, HttpStatus.CREATED); // Retorna 201 Created
    }

    // Endpoint para listar todas as sessões de evento (GET /api/sessoes-evento)
    @GetMapping
    public ResponseEntity<List<SessaoEvento>> listarTodasSessoesEventos() {
        List<SessaoEvento> sessoes = sessaoEventoService.buscarTodasSessoesEventos();
        return new ResponseEntity<>(sessoes, HttpStatus.OK); // Retorna 200 OK
    }

    // Endpoint para buscar uma sessão de evento por ID (GET /api/sessoes-evento/{id})
    @GetMapping("/{id}")
    public ResponseEntity<SessaoEvento> buscarSessaoEventoPorId(@PathVariable Long id) {
        return sessaoEventoService.buscarSessaoEventoPorId(id)
                .map(sessao -> new ResponseEntity<>(sessao, HttpStatus.OK)) // Retorna 200 OK se encontrado
                .orElseThrow(() -> new ResourceNotFoundException("Sessão de Evento não encontrada com ID: " + id)); // Retorna 404 Not Found
    }

    // Endpoint para listar sessões por ID do Evento Pai (GET /api/sessoes-evento/evento/{eventoId})
    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<List<SessaoEvento>> listarSessoesPorEventoPai(@PathVariable Long eventoId) {
        List<SessaoEvento> sessoes = sessaoEventoService.buscarSessoesPorEventoPai(eventoId);
        return new ResponseEntity<>(sessoes, HttpStatus.OK);
    }

    // Endpoint para atualizar uma sessão de evento (PUT /api/sessoes-evento/{id})
    @PutMapping("/{id}")
    public ResponseEntity<SessaoEvento> atualizarSessaoEvento(@PathVariable Long id, @Valid @RequestBody SessaoEvento sessaoEvento) {
        try {
            SessaoEvento sessaoAtualizada = sessaoEventoService.atualizarSessaoEvento(id, sessaoEvento);
            return new ResponseEntity<>(sessaoAtualizada, HttpStatus.OK); // Retorna 200 OK
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Retorna 404 Not Found
        }
    }

    // Endpoint para deletar uma sessão de evento (DELETE /api/sessoes-evento/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarSessaoEvento(@PathVariable Long id) {
        sessaoEventoService.deletarSessaoEvento(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Retorna 204 No Content
    }
}