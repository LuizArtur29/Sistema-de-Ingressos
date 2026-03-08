package com.vendaingressos.controller;

import com.vendaingressos.dto.EventoResponse;
import com.vendaingressos.exception.BadRequestException; // Adicionado para validação de data
import com.vendaingressos.exception.ResourceNotFoundException;
import com.vendaingressos.model.Evento;
import com.vendaingressos.service.EventoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/eventos")
@CrossOrigin(origins = "http://localhost:8080")
public class EventoController {

    private final EventoService eventoService;

    @Autowired
    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @PostMapping
    public ResponseEntity<EventoResponse> criarEvento(@Valid @RequestBody Evento evento) {
        Evento novoEvento = eventoService.salvarEvento(evento);
        return new ResponseEntity<>(new EventoResponse(novoEvento), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<EventoResponse>> listarTodosEventos() {
        List<Evento> eventos = eventoService.buscarTodosEventos();
        List<EventoResponse> eventosDTO = eventos.stream()
                .map(EventoResponse::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(eventosDTO, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoResponse> buscarEventoPorId(@PathVariable Long id) {
        return eventoService.buscarEventoPorId(id)
                .map(evento -> new ResponseEntity<>(new EventoResponse(evento), HttpStatus.OK))
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado com ID: " + id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventoResponse> atualizarEvento(@PathVariable Long id, @Valid @RequestBody Evento evento) {
        Evento eventoAtualizado = eventoService.atualizarEvento(id, evento);
        return new ResponseEntity<>(new EventoResponse(eventoAtualizado), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarEvento(@PathVariable Long id) {
        eventoService.deletarEvento(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}