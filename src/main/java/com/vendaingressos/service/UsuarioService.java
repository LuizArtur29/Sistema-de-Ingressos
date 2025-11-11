package com.vendaingressos.service;

import com.vendaingressos.exception.BadRequestException;
import com.vendaingressos.exception.ResourceNotFoundException;
import com.vendaingressos.model.Compra;
import com.vendaingressos.model.Ingresso;
import com.vendaingressos.model.Usuario;
import com.vendaingressos.redis.UsuarioRedisCache;
import com.vendaingressos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UsuarioService {


    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final CompraService compraService;
    private final UsuarioRedisCache usuarioRedisCache;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, CompraService compraService, UsuarioRedisCache usuarioRedisCache){
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.compraService = compraService;
        this.usuarioRedisCache = usuarioRedisCache;
    }

    @Transactional
    public Usuario cadastrarUsuario(Usuario usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new BadRequestException("Já existe um usuário cadastrado com este e-mail.");
        }
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        Usuario novoUsuario = usuarioRepository.save(usuario);
        usuarioRedisCache.cacheUsuario(novoUsuario);

        return usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public List<Usuario> buscarTodosUsuarios(){
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarUsuarioPorId(Long id){
        Optional<Usuario> cachedUser = usuarioRedisCache.getCachedUsuario(id);
        if(cachedUser.isPresent()) {
            return cachedUser;
        }

        //Para caso não esteja no cache, será procurado no banco de dados e posteriormente salvo no cache antes de retornar
        Optional<Usuario> dbUser = usuarioRepository.findById(id);
        //Se dbUser existir, chame o método cacheUsuario passando o próprio dbUser como parâmetro.
        dbUser.ifPresent(usuarioRedisCache::cacheUsuario);

        return dbUser;
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Transactional
    public Usuario atualizarUsuario(Long id, Usuario usuarioAtualizado) {
        return usuarioRepository.findById(id).map(usuario -> {
            usuario.setNome(usuarioAtualizado.getNome());
            usuario.setCpf(usuarioAtualizado.getCpf());
            usuario.setDataNascimento(usuarioAtualizado.getDataNascimento());
            usuario.setEmail(usuarioAtualizado.getEmail());
            usuario.setEndereco(usuarioAtualizado.getEndereco());
            usuario.setTelefone(usuarioAtualizado.getTelefone());
            if (usuarioAtualizado.getSenha() != null && !usuarioAtualizado.getSenha().isEmpty()) {
                usuario.setSenha(passwordEncoder.encode(usuarioAtualizado.getSenha()));
            }

            Usuario usuarioSalvo = usuarioRepository.save(usuario);

            usuarioRedisCache.invalidateCacheForUsuario(id);
            usuarioRedisCache.cacheUsuario(usuarioSalvo);

            return usuarioSalvo;
        }).orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));
    }

    @Transactional
    public void deletarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário não encontrado com ID: " + id);
        }
        usuarioRepository.deleteById(id);
        usuarioRedisCache.invalidateCacheForUsuario(id);
    }

    @Transactional(readOnly = true)
    public List<Ingresso> listarIngressosPorUsuario(Long usuarioId) {
        // Primeiro, verifica se o usuário existe
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuário não encontrado com ID: " + usuarioId);
        }
        // Busca todas as compras do usuário
        List<Compra> comprasDoUsuario = compraService.buscarComprasPorUsuario(usuarioId);

        // Extrai os ingressos de cada compra
        return comprasDoUsuario.stream()
                .map(Compra::getIngresso)
                .collect(Collectors.toList());
    }
}
