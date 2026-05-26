package com.vendaingressos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendaingressos.config.SecurityConfig;
import com.vendaingressos.dto.TransferenciaRequest;
import com.vendaingressos.filter.JwtRequestFilter;
import com.vendaingressos.model.Transferencia;
import com.vendaingressos.model.Usuario;
import com.vendaingressos.service.CustomUserDetailsService;
import com.vendaingressos.service.TransferenciaService;
import com.vendaingressos.service.UsuarioService;
import com.vendaingressos.util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TransferenciaController.class, properties = {
        "security.cors.allowed-origins=http://localhost:3000",
        "jwt.secret=9a4f632e2225243a612141243161242131231231231231231231231231231231"
})
@Import({SecurityConfig.class, JwtRequestFilter.class})
@ActiveProfiles("test")
class TransferenciaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransferenciaService transferenciaService;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("POST /api/transferencias - Deve retornar 200 OK quando autenticado e dados válidos")
    @WithMockUser(username = "vendedor@email.com", roles = {"USER"})
    void realizarTransferencia_DeveRetornarOk_QuandoAutenticado() throws Exception {
        TransferenciaRequest request = new TransferenciaRequest(10L, 2L, 50.0);

        Usuario mockVendedor = new Usuario();
        mockVendedor.setIdUsuario(1L);
        mockVendedor.setEmail("vendedor@email.com");

        Transferencia mockTransferencia = new Transferencia();
        mockTransferencia.setIdTransferencia(1L);

        when(usuarioService.buscarUsuarioPorEmail("vendedor@email.com")).thenReturn(Optional.of(mockVendedor));
        when(transferenciaService.realizarTransferencia(any(TransferenciaRequest.class), any(Usuario.class)))
                .thenReturn(mockTransferencia);

        mockMvc.perform(post("/api/transferencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/transferencias - Deve retornar 403 Forbidden se o usuário for anônimo devido às restrições de Roles")
    void realizarTransferencia_DeveRetornar403_QuandoNaoAutenticado() throws Exception {
        TransferenciaRequest request = new TransferenciaRequest(10L, 2L, 50.0);

        mockMvc.perform(post("/api/transferencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}