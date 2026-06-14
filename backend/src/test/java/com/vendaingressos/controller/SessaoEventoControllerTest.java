package com.vendaingressos.controller;

import com.vendaingressos.config.SecurityConfig;
import com.vendaingressos.filter.JwtRequestFilter;
import com.vendaingressos.model.Evento;
import com.vendaingressos.model.SessaoEvento;
import com.vendaingressos.service.CustomUserDetailsService;
import com.vendaingressos.service.SessaoEventoService;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SessaoEventoController.class, properties = {
        "security.cors.allowed-origins=http://localhost:3000",
        "jwt.secret=9a4f632e2225243a612141243161242131231231231231231231231231231231"
})
@Import({SecurityConfig.class, JwtRequestFilter.class})
@ActiveProfiles("test")
class SessaoEventoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SessaoEventoService sessaoEventoService;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("GET /api/sessoes-evento/evento/{eventoId} - Deve retornar 200 OK para usuário autenticado")
    @WithMockUser(username = "user@email.com", roles = {"USER"})
    void listarSessoesPorEventoPai_DeveRetornarOk_QuandoUsuarioAutenticado() throws Exception {
        Evento evento = new Evento();
        evento.setId(1L);

        SessaoEvento sessao = new SessaoEvento();
        sessao.setIdSessao(10L);
        sessao.setNomeSessao("Sessao principal");
        sessao.setDataHoraSessao(LocalDateTime.now().plusDays(1));
        sessao.setStatusSessao("ATIVO");
        sessao.setEventoPai(evento);

        when(sessaoEventoService.buscarSessoesPorEventoPai(1L)).thenReturn(List.of(sessao));

        mockMvc.perform(get("/api/sessoes-evento/evento/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/sessoes-evento - Deve retornar 403 Forbidden para usuário sem role ADMIN")
    @WithMockUser(username = "user@email.com", roles = {"USER"})
    void criarSessaoEvento_DeveRetornarForbidden_QuandoUsuarioNaoAdmin() throws Exception {
        mockMvc.perform(post("/api/sessoes-evento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sessaoRequestJson()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/sessoes-evento - Deve retornar 201 Created para usuário ADMIN")
    @WithMockUser(username = "admin@email.com", roles = {"ADMIN"})
    void criarSessaoEvento_DeveRetornarCreated_QuandoAdmin() throws Exception {
        Evento evento = new Evento();
        evento.setId(1L);

        SessaoEvento criada = new SessaoEvento();
        criada.setIdSessao(10L);
        criada.setNomeSessao("Sessao principal");
        criada.setDataHoraSessao(LocalDateTime.now().plusDays(1));
        criada.setStatusSessao("ATIVO");
        criada.setEventoPai(evento);

        when(sessaoEventoService.salvarSessaoEvento(any(SessaoEvento.class))).thenReturn(criada);

        mockMvc.perform(post("/api/sessoes-evento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sessaoRequestJson()))
                .andExpect(status().isCreated());
    }

    private String sessaoRequestJson() {
        return """
                {
                  "nomeSessao": "Sessao principal",
                  "dataHoraSessao": "%s",
                  "statusSessao": "ATIVO",
                  "eventoPai": {
                    "id": 1
                  }
                }
                """.formatted(LocalDateTime.now().plusDays(1));
    }
}
