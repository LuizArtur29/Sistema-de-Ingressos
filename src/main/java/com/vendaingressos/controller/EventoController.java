package com.vendaingressos.controller;

import com.vendaingressos.dto.EventoResponse;
import com.vendaingressos.exception.ResourceNotFoundException;
import com.vendaingressos.model.Evento;
import com.vendaingressos.repository.EventoRepository;
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

    @PostMapping("/upload-banner")
    public ResponseEntity<String> uploadBanner(@RequestParam("arquivo") MultipartFile arquivo) {
        String nomeArquivo = minioService.uploadArquivo(arquivo);
        return ResponseEntity.ok("Arquivo enviado! Nome salvo: " + nomeArquivo);
    }

    @GetMapping("/ver-banner/{nomeArquivo}")
    public ResponseEntity<String> verBanner(@PathVariable String nomeArquivo) {
        String url = minioService.getUrlArquivo(nomeArquivo);
        return ResponseEntity.ok(url);
    }

    @PostMapping("/{id}/upload-banner")
    public ResponseEntity<String> uploadBannerEvento(@PathVariable Long id,
                                                     @RequestParam("arquivo") MultipartFile arquivo) {
        try {
            // 1. Verificar se o evento existe
            Evento evento = eventoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Evento não encontrado!"));

            // 2. Enviar arquivo para o MinIO
            String nomeArquivo = minioService.uploadArquivo(arquivo);

            // 3. (Opcional) Se já tinha uma foto antes, deletar a antiga do MinIO para não acumular lixo
            if (evento.getImagemNome() != null && !evento.getImagemNome().isEmpty()) {
                try {
                    minioService.deletarArquivo(evento.getImagemNome());
                } catch (Exception e) {
                    System.err.println("Aviso: Não foi possível apagar a imagem antiga.");
                }
            }

            // 4. Atualizar o nome da imagem no banco
            evento.setImagemNome(nomeArquivo);
            eventoRepository.save(evento);

            return ResponseEntity.ok("Banner atualizado com sucesso! Nome: " + nomeArquivo);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao salvar banner: " + e.getMessage());
        }
    }

}