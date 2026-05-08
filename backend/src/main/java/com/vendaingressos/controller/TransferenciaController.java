package com.vendaingressos.controller;

import com.vendaingressos.dto.TransferenciaRequest;
import com.vendaingressos.dto.TransferenciaResponse;
import com.vendaingressos.exception.ResourceNotFoundException;
import com.vendaingressos.model.Transferencia;
import com.vendaingressos.model.Usuario;
import com.vendaingressos.service.TransferenciaService;
import com.vendaingressos.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transferencias")
public class TransferenciaController {

    @Autowired
    private TransferenciaService transferenciaService;
    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<TransferenciaResponse> transferir(@Valid @RequestBody TransferenciaRequest request, Authentication authentication) {
        String email = authentication.getName();

        Usuario vendedor = usuarioService.buscarUsuarioPorEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário autenticado não encontrado."));

        Transferencia transferencia = transferenciaService.realizarTransferencia(request, vendedor);

        return ResponseEntity.ok(new TransferenciaResponse(transferencia));
    }
}