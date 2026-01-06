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
            // 1. Verificar se o evento existe (USANDO PADRÃO JDBC - NULL CHECK)
            Evento evento = eventoRepository.buscarPorId(id);

            if (evento == null) {
                throw new ResourceNotFoundException("Evento não encontrado com ID: " + id);
            }

            // 2. Enviar arquivo para o MinIO
            String nomeArquivo = minioService.uploadArquivo(arquivo);

            // 3. Deletar imagem antiga se existir (Limpeza)
            if (evento.getImagemNome() != null && !evento.getImagemNome().isEmpty()) {
                try {
                    minioService.deletarArquivo(evento.getImagemNome());
                } catch (Exception e) {
                    System.err.println("Aviso: Não foi possível apagar a imagem antiga no MinIO.");
                }
            }

            // 4. Atualizar o nome da imagem no objeto
            evento.setImagemNome(nomeArquivo);

            // 5. Atualizar no Banco (USANDO ATUALIZAR, NÃO SAVE)
            eventoRepository.atualizar(evento);

            // 6. IMPORTANTE: Invalidar o Cache do Redis!
            // Como mexemos no banco direto pelo repository, o Redis não sabe que mudou.
            // Precisamos avisar ele:
            eventoRedisCache.invalidateCacheForEvento(id);
            eventoRedisCache.cacheEvento(evento);

            return ResponseEntity.ok("Banner atualizado com sucesso! Nome: " + nomeArquivo);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erro ao salvar banner: " + e.getMessage());
        }
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