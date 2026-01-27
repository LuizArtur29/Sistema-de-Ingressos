package com.vendaingressos.service;

import com.vendaingressos.exception.BadRequestException;
import com.vendaingressos.exception.ResourceNotFoundException;
import com.vendaingressos.model.Evento;
import com.vendaingressos.redis.EventoRedisCache;
import com.vendaingressos.repository.jdbc.EventoRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    private final MinioService minioService;
    private final EventoRedisCache eventoRedisCache;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Autowired
    public EventoService(EventoRepository eventoRepository, EventoRedisCache eventoRedisCache, MinioService minioService) {
        this.eventoRepository = eventoRepository;
        this.eventoRedisCache = eventoRedisCache;
        this.minioService = minioService;
    }

    @Transactional
    public Evento salvarEvento(Evento evento) {
        if (evento.getDataFim() != null && evento.getDataInicio() != null &&
                evento.getDataFim().isBefore(evento.getDataInicio())) {
            throw new BadRequestException("A data de fim do evento não pode ser anterior à data de início.");
        }

        eventoRepository.salvar(evento);

        // Cache
        eventoRedisCache.cacheEvento(evento);

        return evento;
    }

    @Transactional(readOnly = true)
    public List<Evento> buscarTodosEventos() {
        return eventoRepository.listarTodos();
    }

    @Transactional(readOnly = true)
    public Optional<Evento> buscarEventoPorId(Long id) {
        Optional<Evento> cachedEvento = eventoRedisCache.getCachedEvento(id);
        if (cachedEvento.isPresent()) {
            return cachedEvento;
        }

        Evento evento = eventoRepository.buscarPorId(id);

        if (evento != null) {
            eventoRedisCache.cacheEvento(evento);
            return Optional.of(evento);
        }

        return Optional.empty();
    }

    @Transactional
    public void deletarEvento(Long id) {
        if (eventoRepository.buscarPorId(id) == null) {
            throw new ResourceNotFoundException("Evento não encontrado com ID: " + id);
        }

        eventoRepository.deletar(id);
        eventoRedisCache.invalidateCacheForEvento(id);
    }

    @Transactional
    public Evento atualizarEvento(Long id, Evento eventoAtualizado) {
        // Busca o evento existente
        Evento eventoExistente = eventoRepository.buscarPorId(id);

        if (eventoExistente == null) {
            throw new ResourceNotFoundException("Evento não encontrado com ID: " + id);
        }

        //  Atualiza os dados do objeto existente
        eventoExistente.setNome(eventoAtualizado.getNome());
        eventoExistente.setDescricao(eventoAtualizado.getDescricao());
        eventoExistente.setDataInicio(eventoAtualizado.getDataInicio());
        eventoExistente.setDataFim(eventoAtualizado.getDataFim());
        eventoExistente.setLocal(eventoAtualizado.getLocal());
        eventoExistente.setCapacidadeTotal(eventoAtualizado.getCapacidadeTotal());
        eventoExistente.setStatus(eventoAtualizado.getStatus());

        // Mantém a imagem antiga se a nova não for enviada
        if (eventoAtualizado.getImagemNome() != null) {
            eventoExistente.setImagemNome(eventoAtualizado.getImagemNome());
        }

        //  Chama o update do JDBC
        eventoRepository.atualizar(eventoExistente);

        // Atualiza Cache
        eventoRedisCache.invalidateCacheForEvento(id);
        eventoRedisCache.cacheEvento(eventoExistente);

        return eventoExistente;
    }

    @Transactional(readOnly = true)
    public Double obterReceitaTotal(Long idEvento) {
        if (eventoRepository.buscarPorId(idEvento) == null) {
            throw new ResourceNotFoundException("Evento não encontrado com ID: " + idEvento);
        }

        return eventoRepository.calcularReceitaTotal(idEvento);
    }

    public List<Evento> buscarEventosProximos(double lat, double lng, double raioMetros) {
        Point pontoBusca = geometryFactory.createPoint(new Coordinate(lng, lat));
        return eventoRepository.buscarEventoNoRaio(pontoBusca, raioMetros);
    }

    public Double calcularDistanciaAteEvento(Long id, double lat, double lng) {
        Point pontoCriado = geometryFactory.createPoint(new Coordinate(lng, lat));
        return eventoRepository.medirDistanciaEntrePontos(id, pontoCriado);
    }

    @Transactional
    public String uploadBannerEvento(Long id, MultipartFile arquivo) {

        try {
            Evento evento = eventoRepository.buscarPorId(id);
            if (evento == null) {
                throw new ResourceNotFoundException("Evento não encontrado");
            }

            String novoNomeArquivo = minioService.uploadArquivo(arquivo);

            //Remoção física do arquivo antigo no MinIO (para não ocupar espaço)
            if (evento.getImagemNome() != null) {
                minioService.deletarArquivo(evento.getImagemNome());
            }

            evento.setImagemNome(novoNomeArquivo);
            eventoRepository.atualizar(evento);

            // Sincronização com Redis
            eventoRedisCache.invalidateCacheForEvento(id);
            eventoRedisCache.cacheEvento(evento);

            return novoNomeArquivo;
        } catch (Exception e) {
            throw new RuntimeException("Falha ao processar upload do banner: " + e.getMessage());
        }
    }

}