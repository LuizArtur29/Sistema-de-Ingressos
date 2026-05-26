package com.vendaingressos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendaingressos.config.SecurityConfig;
import com.vendaingressos.dto.CompraRequest;
import com.vendaingressos.filter.JwtRequestFilter;
import com.vendaingressos.model.Compra;
import com.vendaingressos.model.Usuario;
import com.vendaingressos.model.enums.MetodoPagamento;
import com.vendaingressos.service.CompraService;
import com.vendaingressos.service.CustomUserDetailsService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CompraController.class, properties = {
        "security.cors.allowed-origins=http://localhost:3000",
        "jwt.secret=9a4f632e2225243a612141243161242131231231231231231231231231231231"
})
@Import({SecurityConfig.class, JwtRequestFilter.class})
@ActiveProfiles("test")
class CompraControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CompraService compraService;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("POST /api/compras - Deve retornar 201 Created quando usuário estiver autenticado com token válido")
    @WithMockUser(username = "user@email.com", roles = {"USER"})
    void realizarCompra_DeveRetornarCreated_QuandoAutenticado() throws Exception {
        // Arrange
        CompraRequest request = new CompraRequest();
        request.setUsuarioID(1L);
        request.setIngressoID(100L);
        request.setQuantidadeIngressos(1);
        request.setMetodoPagamento(MetodoPagamento.PIX);
        request.setMeiaEntrada(false);

        Usuario mockUsuario = new Usuario();
        mockUsuario.setIdUsuario(1L);

        Compra mockCompra = new Compra();
        mockCompra.setIdCompra(1L);
        mockCompra.setStatus("Concluida");
        mockCompra.setUsuario(mockUsuario);

        when(compraService.realizarCompra(anyLong(), anyLong(), anyInt(), any(MetodoPagamento.class), anyBoolean()))
                .thenReturn(mockCompra);

        mockMvc.perform(post("/api/compras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /api/compras - Deve retornar 403 Forbidden se requisição for anônima devido às restrições de Roles")
    void realizarCompra_DeveRetornar403_QuandoNaoAutenticado() throws Exception {
        CompraRequest request = new CompraRequest();
        request.setUsuarioID(1L);
        request.setIngressoID(100L);
        request.setQuantidadeIngressos(1);
        request.setMetodoPagamento(MetodoPagamento.PIX);

        mockMvc.perform(post("/api/compras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}