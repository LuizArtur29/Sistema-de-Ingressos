package com.vendaingressos.service;

import com.vendaingressos.exception.ResourceNotFoundException;
import com.vendaingressos.model.Usuario;
import com.vendaingressos.model.mongo.AvaliacaoEvento;
import com.vendaingressos.repository.EventoRepository;
import com.vendaingressos.repository.UsuarioRepository;
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
        if (!eventoRepository.existsById(idEvento)) {
            throw new ResourceNotFoundException("Evento não encontrado no SQL com ID: " + idEvento);
        }

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        AvaliacaoEvento avaliacao = new AvaliacaoEvento(idEvento, idUsuario, usuario.getNome(),  nota, comentario);
        return avaliacaoRepository.save(avaliacao);
    }

    public List<AvaliacaoEvento> listarAvaliacoesDoEvento(Long idEvento) {
        return avaliacaoRepository.findByIdEvento(idEvento);
    }
}