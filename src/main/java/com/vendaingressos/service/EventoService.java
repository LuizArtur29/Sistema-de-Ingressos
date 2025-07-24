package com.vendaingressos.service; // Certifique-se de que o pacote está correto

import com.vendaingressos.model.Evento; // Importa a entidade Evento
import com.vendaingressos.repository.EventoRepository; // Importa o EventoRepository
import org.springframework.beans.factory.annotation.Autowired; // Para injeção de dependência
import org.springframework.stereotype.Service; // Indica que esta classe é um componente de serviço
import org.springframework.transaction.annotation.Transactional; // Para gerenciamento de transações

import java.util.List;
import java.util.Optional;

@Service // Indica ao Spring que esta classe é um componente de serviço
public class EventoService {

    private final EventoRepository eventoRepository; // Injeta o repositório

    // Construtor para injeção de dependência do EventoRepository
    @Autowired // Opcional a partir do Spring 4.3 para injeção via construtor
    public EventoService(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    // Método para salvar um novo evento ou atualizar um existente
    @Transactional // Garante que a operação seja atômica no banco de dados
    public Evento salvarEvento(Evento evento) {
        // Aqui você pode adicionar lógica de negócio antes de salvar,
        // como validações, preenchimento de campos automáticos, etc.
        // Exemplo: if (evento.getDataHora().isBefore(LocalDateTime.now())) throw new IllegalArgumentException("Data do evento não pode ser no passado");
        return eventoRepository.save(evento);
    }

    // Método para buscar todos os eventos
    @Transactional(readOnly = true) // Otimiza a transação para leitura
    public List<Evento> buscarTodosEventos() {
        return eventoRepository.findAll();
    }

    // Método para buscar um evento por ID
    @Transactional(readOnly = true)
    public Optional<Evento> buscarEventoPorId(Long id) {
        return eventoRepository.findById(id);
    }

    // Método para deletar um evento por ID
    @Transactional
    public void deletarEvento(Long id) {
        // Aqui você pode adicionar lógica de negócio antes de deletar,
        // como verificar se o evento tem ingressos vendidos, etc.
        eventoRepository.deleteById(id);
    }

    // Método para atualizar um evento existente
    @Transactional
    public Evento atualizarEvento(Long id, Evento eventoAtualizado) {
        return eventoRepository.findById(id).map(evento -> {
            evento.setNome(eventoAtualizado.getNome());
            evento.setDescricao(eventoAtualizado.getDescricao());
            evento.setDataHora(eventoAtualizado.getDataHora());
            evento.setLocal(eventoAtualizado.getLocal());
            evento.setCapacidadeTotal(eventoAtualizado.getCapacidadeTotal());
            evento.setStatus(eventoAtualizado.getStatus());
            // Adicione outros campos que podem ser atualizados
            return eventoRepository.save(evento);
        }).orElseThrow(() -> new RuntimeException("Evento não encontrado com ID: " + id));
        // Em uma aplicação real, você usaria uma exceção mais específica, como ResourceNotFoundException
    }
}