package com.vendaingressos.controller;

import com.vendaingressos.config.SecurityConfig;
import com.vendaingressos.filter.JwtRequestFilter;
import com.vendaingressos.service.CustomUserDetailsService;
import com.vendaingressos.service.IngressoService;
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

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = IngressoController.class, properties = {
        "security.cors.allowed-origins=http://localhost:3000",
        "jwt.secret=9a4f632e2225243a612141243161242131231231231231231231231231231231"
})
@Import({SecurityConfig.class, JwtRequestFilter.class})
@ActiveProfiles("test")
class IngressoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IngressoService ingressoService;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("GET /api/ingressos/sessoes/{sessaoEventoId} - Deve retornar 200 OK para usuário autenticado")
    @WithMockUser(username = "user@email.com", roles = {"USER"})
    void listarPorSessao_DeveRetornarOk_QuandoUsuarioAutenticado() throws Exception {
        when(ingressoService.listarIngressosPorSessaoEvento(2L)).thenReturn(List.of());

        mockMvc.perform(get("/api/ingressos/sessoes/2"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/ingressos/sessoes/{sessaoEventoId}/tipos/{tipoIngressoId} - Deve retornar 403 Forbidden para usuário sem role ADMIN")
    @WithMockUser(username = "user@email.com", roles = {"USER"})
    void criarIngresso_DeveRetornarForbidden_QuandoUsuarioNaoAdmin() throws Exception {
        mockMvc.perform(post("/api/ingressos/sessoes/2/tipos/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "preco": 100.0
                                }
                                """))
                .andExpect(status().isForbidden());
    }
}
