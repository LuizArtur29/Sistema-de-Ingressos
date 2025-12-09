package com.vendaingressos.service;

import com.vendaingressos.exception.ResourceNotFoundException;
import com.vendaingressos.model.Evento;
import com.vendaingressos.model.Usuario;
import com.vendaingressos.model.mongo.AvaliacaoEvento;
import com.vendaingressos.repository.jdbc.EventoRepository;
import com.vendaingressos.repository.jdbc.UsuarioRepository;
import com.vendaingressos.repository.mongo.AvaliacaoEventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AvaliacaoService {

    @Autowired
    private AvaliacaoEventoRepository avaliacaoRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public AvaliacaoEvento avaliarEvento(Long idEvento, Long idUsuario, int nota, String comentario) {
        Evento evento = eventoRepository.buscarPorId(idEvento);

        if (evento == null) {
            throw new ResourceNotFoundException("Evento não encontrado no SQL com ID: " + idEvento);
        }

        Usuario usuario = usuarioRepository.buscarPorId(idUsuario);

        if (usuario == null) {
            throw new ResourceNotFoundException("Usuário não encontrado com ID: " + idUsuario);
        }

        AvaliacaoEvento avaliacaoEvento = new AvaliacaoEvento(
                idEvento,
                idUsuario,
                usuario.getNome(),
                nota,
                comentario
        );

        return avaliacaoRepository.save(avaliacaoEvento);
    }

    public List<AvaliacaoEvento> listarAvaliacoesDoEvento(Long idEvento) {
        if (eventoRepository.buscarPorId(idEvento) == null) {
            throw new ResourceNotFoundException("Evento não existe: " + idEvento);
        }

        return avaliacaoRepository.findByIdEvento(idEvento);
    }
}