package com.vendaingressos.controller;

import com.vendaingressos.dto.AuthenticationRequest;
import com.vendaingressos.dto.AuthenticationResponse;
import com.vendaingressos.service.LogService;
import com.vendaingressos.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:8080")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private LogService logService;

    @PostMapping("/login")
    public ResponseEntity<?> createAutheticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getSenha())
            );

            final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
            final String jwt = jwtUtil.generateToken(userDetails);

            logService.registrarAtividade(authenticationRequest.getEmail(), "LOGIN_SUCESSO", Map.of("origem", "web"));

            return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }
}