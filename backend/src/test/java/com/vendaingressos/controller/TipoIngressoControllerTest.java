package com.vendaingressos.controller;

import com.vendaingressos.config.SecurityConfig;
import com.vendaingressos.filter.JwtRequestFilter;
import com.vendaingressos.model.TipoIngresso;
import com.vendaingressos.repository.SessaoEventoRepository;
import com.vendaingressos.service.CustomUserDetailsService;
import com.vendaingressos.service.TipoIngressoService;
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

@WebMvcTest(controllers = TipoIngressoController.class, properties = {
        "security.cors.allowed-origins=http://localhost:3000",
        "jwt.secret=9a4f632e2225243a612141243161242131231231231231231231231231231231"
})
@Import({SecurityConfig.class, JwtRequestFilter.class})
@ActiveProfiles("test")
class TipoIngressoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TipoIngressoService tipoIngressoService;

    @MockBean
    private SessaoEventoRepository sessaoEventoRepository;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("GET /api/tipos-ingresso/sessao/{sessaoId} - Deve retornar 200 OK para usuário autenticado")
    @WithMockUser(username = "user@email.com", roles = {"USER"})
    void listar_DeveRetornarOk_QuandoUsuarioAutenticado() throws Exception {
        TipoIngresso tipo = new TipoIngresso("Pista", 100.0, 10, 10, 1);

        when(tipoIngressoService.listarPorSessao(2L)).thenReturn(List.of(tipo));

        mockMvc.perform(get("/api/tipos-ingresso/sessao/2"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/tipos-ingresso - Deve retornar 403 Forbidden para usuário sem role ADMIN")
    @WithMockUser(username = "user@email.com", roles = {"USER"})
    void criar_DeveRetornarForbidden_QuandoUsuarioNaoAdmin() throws Exception {
        mockMvc.perform(post("/api/tipos-ingresso")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nomeSetor": "Pista",
                                  "preco": 100.0,
                                  "quantidadeTotal": 10,
                                  "lote": 1,
                                  "sessaoId": 2
                                }
                                """))
                .andExpect(status().isForbidden());
    }
}
