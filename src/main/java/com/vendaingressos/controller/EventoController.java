package com.vendaingressos.controller; // Certifique-se de que o pacote está correto

import com.vendaingressos.exception.ResourceNotFoundException;
import com.vendaingressos.model.Evento;     // Importa a entidade Evento
import com.vendaingressos.service.EventoService; // Importa o serviço de Evento
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;             // Para códigos de status HTTP
import org.springframework.http.ResponseEntity;         // Para retornar respostas HTTP completas
import org.springframework.web.bind.annotation.*;     // Anotações para RESTful

import java.util.List;

@RestController // Indica ao Spring que esta classe é um controlador REST
@RequestMapping("/api/eventos") // Define o caminho base para todos os endpoints deste controlador
@CrossOrigin(origins = "http://localhost:8080") // Permite requisições de um frontend rodando em localhost:8080
public class EventoController {

    private final EventoService eventoService; // Injeta o serviço

    // Construtor para injeção de dependência do EventoService
    @Autowired
    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    // Endpoint para criar um novo evento (POST /api/eventos)
    @PostMapping
    public ResponseEntity<Evento> criarEvento(@Valid @RequestBody Evento evento) {
        Evento novoEvento = eventoService.salvarEvento(evento);
        return new ResponseEntity<>(novoEvento, HttpStatus.CREATED); // Retorna 201 Created
    }

    // Endpoint para listar todos os eventos (GET /api/eventos)
    @GetMapping
    public ResponseEntity<List<Evento>> listarTodosEventos() {
        List<Evento> eventos = eventoService.buscarTodosEventos();
        return new ResponseEntity<>(eventos, HttpStatus.OK); // Retorna 200 OK
    }

    // Endpoint para buscar um evento por ID (GET /api/eventos/{id})
    @GetMapping("/{id}")
    public ResponseEntity<Evento> buscarEventoPorId(@PathVariable Long id) {
        return eventoService.buscarEventoPorId(id)
                .map(evento -> new ResponseEntity<>(evento, HttpStatus.OK)) // Retorna 200 OK se encontrado
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado com ID: " + id)); // Retorna 404 Not Found se não encontrado
    }

    // Endpoint para atualizar um evento (PUT /api/eventos/{id})
    @PutMapping("/{id}")
    public ResponseEntity<Evento> atualizarEvento(@PathVariable Long id, @Valid @RequestBody Evento evento) {
        try {
            Evento eventoAtualizado = eventoService.atualizarEvento(id, evento);
            return new ResponseEntity<>(eventoAtualizado, HttpStatus.OK); // Retorna 200 OK
        } catch (RuntimeException e) { // Captura a exceção de evento não encontrado do serviço
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Retorna 404 Not Found
        }
    }

    // Endpoint para deletar um evento (DELETE /api/eventos/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarEvento(@PathVariable Long id) {
        eventoService.deletarEvento(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Retorna 204 No Content
    }
}