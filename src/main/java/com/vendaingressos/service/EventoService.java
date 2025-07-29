package com.vendaingressos.service;

import com.vendaingressos.exception.BadRequestException;
import com.vendaingressos.exception.ResourceNotFoundException;
import com.vendaingressos.model.Evento;
import com.vendaingressos.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    // O CompraService não precisa mais ser injetado aqui para o método temIngressosDisponiveis
    // pois a lógica de capacidade agora é por SessaoEvento.
    // private final CompraService compraService;

    @Autowired
    public EventoService(EventoRepository eventoRepository /*, CompraService compraService*/) {
        this.eventoRepository = eventoRepository;
        // this.compraService = compraService;
    }

    @Transactional
    public Evento salvarEvento(Evento evento) {
        // Validação da data: garantir que dataFim não seja antes de dataInicio
        if (evento.getDataFim().isBefore(evento.getDataInicio())) {
            throw new BadRequestException("A data de fim do evento não pode ser anterior à data de início.");
        }
        return eventoRepository.save(evento);
    }

    @Transactional(readOnly = true)
    public List<Evento> buscarTodosEventos() {
        return eventoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Evento> buscarEventoPorId(Long id) {
        return eventoRepository.findById(id);
    }

    @Transactional
    public void deletarEvento(Long id) {
        if (!eventoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Evento não encontrado com ID: " + id);
        }
        eventoRepository.deleteById(id);
    }

    @Transactional
    public Evento atualizarEvento(Long id, Evento eventoAtualizado) {
        return eventoRepository.findById(id).map(evento -> {
            evento.setNome(eventoAtualizado.getNome());
            evento.setDescricao(eventoAtualizado.getDescricao());
            evento.setDataInicio(eventoAtualizado.getDataInicio()); // Atualizado
            evento.setDataFim(eventoAtualizado.getDataFim()); // Atualizado
            evento.setLocal(eventoAtualizado.getLocal());
            evento.setCapacidadeTotal(eventoAtualizado.getCapacidadeTotal());
            evento.setStatus(eventoAtualizado.getStatus());
            // Não há mais listaIngressos diretamente no Evento
            return eventoRepository.save(evento);
        }).orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado com ID: " + id));
    }

    // Este método temIngressosDisponiveis precisa ser reavaliado ou removido,
    // pois a disponibilidade agora é por sessão.
    // Se a intenção for verificar se *qualquer* sessão tem ingressos,
    // a lógica precisará ser mais complexa. Por enquanto, vou remover.
    /*
    @Transactional(readOnly = true)
    public boolean temIngressosDisponiveis(Long eventoId) {
        Optional<Evento> eventoOptional = eventoRepository.findById(eventoId);
        if (eventoOptional.isEmpty()) {
            throw new RuntimeException("Evento não encontrado com ID: " + eventoId);
        }
        Evento evento = eventoOptional.get();
        // A contagem de ingressos vendidos agora seria por SessaoEvento
        // long ingressosVendidos = compraService.contarIngressosVendidos(eventoId);
        // return evento.getCapacidadeTotal() > ingressosVendidos;
        return false; // Lógica temporária, precisa ser ajustada ou removida.
    }
    */
}