package com.vendaingressos.service;

import com.vendaingressos.exception.BadRequestException;
import com.vendaingressos.exception.ResourceNotFoundException;
import com.vendaingressos.model.Compra;
import com.vendaingressos.model.Ingresso;
import com.vendaingressos.model.Usuario;
import com.vendaingressos.redis.UsuarioRedisCache;
import com.vendaingressos.repository.jdbc.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    //Ela transforma um dado sensível em um código matemático seguro que só pode ser validado, nunca revertido.
    private final PasswordEncoder passwordEncoder;
    private final CompraService compraService;
    private final UsuarioRedisCache usuarioRedisCache;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          CompraService compraService,
                          UsuarioRedisCache usuarioRedisCache){
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.compraService = compraService;
        this.usuarioRedisCache = usuarioRedisCache;
    }

    @Transactional
    public Usuario cadastrarUsuario(Usuario usuario) {
        if (usuarioRepository.buscarPorEmail(usuario.getEmail()).isPresent()) {
            throw new BadRequestException("Já existe um usuário cadastrado com este e-mail.");
        }

        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        usuarioRepository.salvar(usuario);

        usuarioRedisCache.cacheUsuario(usuario);

        return usuario;
    }

    @Transactional(readOnly = true)
    public List<Usuario> buscarTodosUsuarios(){
        return usuarioRepository.listarTodos();
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarUsuarioPorId(Long id){
        // 1. Tenta Cache
        Optional<Usuario> cachedUser = usuarioRedisCache.getCachedUsuario(id);
        if(cachedUser.isPresent()) {
            return cachedUser;
        }

        // 2. Busca no Banco
        Usuario dbUser = usuarioRepository.buscarPorId(id);

        // 3. Se achou, salva no cache e retorna Optional
        if (dbUser != null) {
            usuarioRedisCache.cacheUsuario(dbUser);
            return Optional.of(dbUser);
        }

        return Optional.empty();
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarUsuarioPorEmail(String email) {
        return usuarioRepository.buscarPorEmail(email);
    }

    @Transactional
    public Usuario atualizarUsuario(Long id, Usuario usuarioAtualizado) {
        Usuario usuarioExistente = usuarioRepository.buscarPorId(id);

        if (usuarioExistente == null) {
            throw new ResourceNotFoundException("Usuário não encontrado com ID: " + id);
        }

        usuarioExistente.setNome(usuarioAtualizado.getNome());
        usuarioExistente.setCpf(usuarioAtualizado.getCpf());
        usuarioExistente.setDataNascimento(usuarioAtualizado.getDataNascimento());
        usuarioExistente.setEmail(usuarioAtualizado.getEmail());
        usuarioExistente.setEndereco(usuarioAtualizado.getEndereco());
        usuarioExistente.setTelefone(usuarioAtualizado.getTelefone());

        if (usuarioAtualizado.getSenha() != null && !usuarioAtualizado.getSenha().isEmpty()) {
            usuarioExistente.setSenha(passwordEncoder.encode(usuarioAtualizado.getSenha()));
        }

        usuarioRepository.atualizar(usuarioExistente);

        usuarioRedisCache.invalidateCacheForUsuario(id);
        usuarioRedisCache.cacheUsuario(usuarioExistente);

        return usuarioExistente;
    }

    @Transactional
    public void deletarUsuario(Long id) {
        if (usuarioRepository.buscarPorId(id) == null) {
            throw new ResourceNotFoundException("Usuário não encontrado com ID: " + id);
        }

        usuarioRepository.deletar(id);
        usuarioRedisCache.invalidateCacheForUsuario(id);
    }

    @Transactional(readOnly = true)
    public List<Ingresso> listarIngressosPorUsuario(Long usuarioId) {
        if (usuarioRepository.buscarPorId(usuarioId) == null) {
            throw new ResourceNotFoundException("Usuário não encontrado com ID: " + usuarioId);
        }

        List<Compra> comprasDoUsuario = compraService.buscarComprasPorUsuario(usuarioId);

        return comprasDoUsuario.stream()
                .map(Compra::getIngresso)
                .collect(Collectors.toList());
    }
}