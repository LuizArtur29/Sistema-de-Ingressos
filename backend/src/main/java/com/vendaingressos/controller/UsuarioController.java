package com.vendaingressos.controller;

import com.vendaingressos.dto.UsuarioCreateRequest;
import com.vendaingressos.dto.usuario.UsuarioAllResponse;
import com.vendaingressos.dto.usuario.UsuarioPerfilResponse;
import com.vendaingressos.dto.usuario.UsuarioAdminResponse;
import com.vendaingressos.dto.UsuarioUpdateRequest;
import com.vendaingressos.exception.ResourceNotFoundException;
import com.vendaingressos.model.Usuario;
import com.vendaingressos.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Endpoint para cadastrar um novo usuário (POST /api/usuarios)
    @PostMapping
    public ResponseEntity<UsuarioPerfilResponse> cadastrarUsuario(@Valid @RequestBody UsuarioCreateRequest dto) {
        Usuario novoUsuario = usuarioService.cadastrarUsuario(dto);
        return new ResponseEntity<>(new UsuarioPerfilResponse(novoUsuario), HttpStatus.CREATED);
    }

    // Endpoint para listar todos os usuários (GET /api/usuarios)
    @GetMapping
    public ResponseEntity<List<UsuarioAllResponse>> listarTodosUsuarios() {
        List<Usuario> usuarios = usuarioService.buscarTodosUsuarios();
        List<UsuarioAllResponse> usuariosDTO = usuarios.stream()
                .map(UsuarioAllResponse::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(usuariosDTO, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioAdminResponse> buscarUsuarioPorId(@PathVariable Long id) {
       return usuarioService.buscarUsuarioPorId(id)
               .map(usuario -> new ResponseEntity<>(new UsuarioAdminResponse(usuario), HttpStatus.OK))
               .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioAdminResponse> buscarUsuarioPorEmail(@PathVariable String email) {
        return usuarioService.buscarUsuarioPorEmail(email)
                .map(usuario -> new ResponseEntity<>(new UsuarioAdminResponse(usuario), HttpStatus.OK))
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com e-mail: " + email));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioPerfilResponse> atualizarUsuario(@PathVariable Long id, @Valid @RequestBody UsuarioUpdateRequest dto) {
        Usuario usuarioAtualizado = usuarioService.atualizarUsuario(id, dto);
        return new ResponseEntity<>(new UsuarioPerfilResponse(usuarioAtualizado), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) {
        usuarioService.deletarUsuario(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioPerfilResponse> meuPerfil(Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioService.buscarUsuarioPorEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário autenticado não encontrado."));
        return ResponseEntity.ok(new UsuarioPerfilResponse(usuario));
    }

    @PutMapping("/me")
    public ResponseEntity<UsuarioPerfilResponse> atualizarMeuPerfil(Authentication authentication, @Valid @RequestBody UsuarioUpdateRequest dto) {
        String email = authentication.getName();
        Usuario atual = usuarioService.buscarUsuarioPorEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário autenticado não encontrado."));
        Usuario atualizado = usuarioService.atualizarUsuario(atual.getIdUsuario(), dto);
        return ResponseEntity.ok(new UsuarioPerfilResponse(atualizado));
    }
}