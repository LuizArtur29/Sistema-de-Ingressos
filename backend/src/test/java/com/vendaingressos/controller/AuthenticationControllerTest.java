package com.vendaingressos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendaingressos.config.SecurityConfig;
import com.vendaingressos.dto.AuthenticationRequest;
import com.vendaingressos.filter.JwtRequestFilter;
import com.vendaingressos.service.CustomUserDetailsService;
import com.vendaingressos.util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthenticationController.class, properties = {
        "security.cors.allowed-origins=http://localhost:3000",
        "jwt.secret=9a4f632e2225243a612141243161242131231231231231231231231231231231"
})
@Import({SecurityConfig.class, JwtRequestFilter.class})
@ActiveProfiles("test")
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("POST /api/auth/login - Deve retornar 200 OK e gerar token com credenciais válidas")
    void login_DeveRetornarOk_QuandoCredenciaisValidas() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("user@email.com", "senha123");
        var userDetails = new User("user@email.com", "senha123", new ArrayList<>());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userDetailsService.loadUserByUsername("user@email.com")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("mocked-jwt-token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/auth/login - Deve retornar 401 Unauthorized quando a senha ou usuário estiver incorreto")
    void login_DeveRetornar401_QuandoCredenciaisIncorretas() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("user@email.com", "senha_errada");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Usuário ou senha inválidos"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}