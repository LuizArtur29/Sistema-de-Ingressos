package com.vendaingressos.controller;

import com.vendaingressos.dto.EventoResponse;
import com.vendaingressos.exception.ResourceNotFoundException;
import com.vendaingressos.model.Evento;
import com.vendaingressos.redis.EventoRedisCache;
import com.vendaingressos.repository.jdbc.EventoRepository;
import com.vendaingressos.service.EventoService;
import com.vendaingressos.service.MinioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/eventos")
@CrossOrigin(origins = "http://localhost:8080")
public class EventoController {

    private final EventoService eventoService;

    @Autowired
    private MinioService minioService;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private EventoRedisCache eventoRedisCache;

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

    @PostMapping("/{idEvento}/upload-banner")
    public ResponseEntity<String> uploadBanner(@PathVariable Long idEvento, @RequestParam("arquivo") MultipartFile arquivo) {
        try {
            String nomeArquivo = eventoService.uploadBannerEvento(idEvento, arquivo);
            return ResponseEntity.ok("Banner atualizado com sucesso: " + nomeArquivo);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro: " + e.getMessage());
        }
    }

    @GetMapping("/{idEvento}/url-imagem")
    public String getUrlImagemEvento(@PathVariable Long idEvento) {
        Evento evento = eventoRepository.buscarPorId(idEvento);
        return minioService.getUrlArquivo(evento.getImagemNome());
    }

    @DeleteMapping("/{idEvento}/delete-imagem")
    public void deletarArquivoImagem(@PathVariable Long idEvento) {
        Evento evento = eventoRepository.buscarPorId(idEvento);
        minioService.deletarArquivo(evento.getImagemNome());
        evento.setImagemNome(null);
        eventoRepository.atualizar(evento);
    }

    @GetMapping("/{id}/receita")
    public ResponseEntity<Double> obterReceitaEvento(@PathVariable Long id) {
        Double receita = eventoService.obterReceitaTotal(id);
        return new ResponseEntity<>(receita, HttpStatus.OK);
    }

    @GetMapping("/proximos")
    public ResponseEntity<List<Evento>> listarProximos(@RequestParam double lat, @RequestParam double lng, @RequestParam(defaultValue = "1000") double raio) {
        List<Evento> eventos = eventoService.buscarEventosProximos(lat, lng, raio);
        return ResponseEntity.ok(eventos);
    }

    @GetMapping("/{id}/distancia")
    public ResponseEntity<Double> obterDistancia(@PathVariable Long id, @RequestParam double lat, @RequestParam double lng) {
        Double distancia = eventoService.calcularDistanciaAteEvento(id, lat, lng);
        return ResponseEntity.ok(distancia);
    }

}