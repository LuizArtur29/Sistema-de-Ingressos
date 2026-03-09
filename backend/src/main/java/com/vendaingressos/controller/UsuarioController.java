package com.vendaingressos.controller;

import com.vendaingressos.dto.UsuarioResponse;
import com.vendaingressos.exception.ResourceNotFoundException;
import com.vendaingressos.model.Usuario;
import com.vendaingressos.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios") // Define o caminho base para os endpoints de usuário
@CrossOrigin(origins = "http://localhost:8080")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Endpoint para cadastrar um novo usuário (POST /api/usuarios)
    @PostMapping
    public ResponseEntity<UsuarioResponse> cadastrarUsuario(@Valid @RequestBody Usuario usuario) {
        Usuario novoUsuario = usuarioService.cadastrarUsuario(usuario);
        return new ResponseEntity<>(new UsuarioResponse(novoUsuario), HttpStatus.CREATED);
    }

    // Endpoint para listar todos os usuários (GET /api/usuarios)
    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listarTodosUsuarios() {
        List<Usuario> usuarios = usuarioService.buscarTodosUsuarios();
        List<UsuarioResponse> usuariosDTO = usuarios.stream()
                .map(UsuarioResponse::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(usuariosDTO, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarUsuarioPorId(@PathVariable Long id) {
       return usuarioService.buscarUsuarioPorId(id)
               .map(usuario -> new ResponseEntity<>(new UsuarioResponse(usuario), HttpStatus.OK))
               .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioResponse> buscarUsuarioPorEmail(@PathVariable String email) {
        return usuarioService.buscarUsuarioPorEmail(email)
                .map(usuario -> new ResponseEntity<>(new UsuarioResponse(usuario), HttpStatus.OK))
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com e-mail: " + email));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> atualizarUsuario(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
        Usuario usuarioAtualizado = usuarioService.atualizarUsuario(id, usuario);
        return new ResponseEntity<>(new UsuarioResponse(usuarioAtualizado), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) {
        usuarioService.deletarUsuario(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}