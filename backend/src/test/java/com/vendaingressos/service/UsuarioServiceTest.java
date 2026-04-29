package com.vendaingressos.service;

import com.vendaingressos.dto.UsuarioCreateRequest;
import com.vendaingressos.dto.UsuarioUpdateRequest;
import com.vendaingressos.exception.BadRequestException;
import com.vendaingressos.exception.ResourceNotFoundException;
import com.vendaingressos.model.Administrador;
import com.vendaingressos.model.Compra;
import com.vendaingressos.model.Ingresso;
import com.vendaingressos.model.Usuario;
import com.vendaingressos.repository.AdministradorRepository;
import com.vendaingressos.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AdministradorRepository administradorRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CompraService compraService;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioBase;

    @BeforeEach
    void setUp() {
        usuarioBase = new Usuario();
        usuarioBase.setIdUsuario(1L);
        usuarioBase.setNome("Joao");
        usuarioBase.setCpf("12345678901");
        usuarioBase.setDataNascimento(LocalDate.of(2000, 1, 1));
        usuarioBase.setEmail("joao@email.com");
        usuarioBase.setSenha("senhaHash");
        usuarioBase.setEndereco("Rua A");
        usuarioBase.setTelefone("11999999999");
    }

    @Test
    void cadastrarUsuario_salvaComSenhaCodificada() {
        UsuarioCreateRequest dto = new UsuarioCreateRequest(
                "Joao",
                "12345678901",
                LocalDate.of(2000, 1, 1),
                "joao@email.com",
                "senha123",
                "Rua A",
                "11999999999"
        );

        when(usuarioRepository.findByEmail(dto.email())).thenReturn(Optional.empty());
        when(administradorRepository.findByEmail(dto.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(dto.senha())).thenReturn("senhaHash");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario salvo = usuarioService.cadastrarUsuario(dto);

        assertEquals("joao@email.com", salvo.getEmail());
        assertEquals("senhaHash", salvo.getSenha());
        verify(passwordEncoder).encode("senha123");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void cadastrarUsuario_lancaQuandoEmailExisteEmUsuario() {
        UsuarioCreateRequest dto = new UsuarioCreateRequest(
                "Joao", "12345678901", LocalDate.of(2000, 1, 1),
                "joao@email.com", "senha123", "Rua A", "11999999999"
        );

        when(usuarioRepository.findByEmail(dto.email())).thenReturn(Optional.of(usuarioBase));

        assertThrows(BadRequestException.class, () -> usuarioService.cadastrarUsuario(dto));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void cadastrarUsuario_lancaQuandoEmailExisteEmAdmin() {
        UsuarioCreateRequest dto = new UsuarioCreateRequest(
                "Joao", "12345678901", LocalDate.of(2000, 1, 1),
                "joao@email.com", "senha123", "Rua A", "11999999999"
        );

        when(usuarioRepository.findByEmail(dto.email())).thenReturn(Optional.empty());
        Administrador admin = new Administrador();
        when(administradorRepository.findByEmail(dto.email()))
                .thenReturn(Optional.of(admin));

        assertThrows(BadRequestException.class, () -> usuarioService.cadastrarUsuario(dto));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void atualizarUsuario_atualizaCamposNaoBlank() {
        UsuarioUpdateRequest dto = new UsuarioUpdateRequest(
                "Novo Nome",
                LocalDate.of(1999, 12, 31),
                "novo@email.com",
                "novaSenha123",
                "Rua B",
                "11888888888"
        );

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioBase));
        when(usuarioRepository.findByEmail("novo@email.com")).thenReturn(Optional.empty());
        when(administradorRepository.findByEmail("novo@email.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("novaSenha123")).thenReturn("senhaNovaHash");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario atualizado = usuarioService.atualizarUsuario(1L, dto);

        assertEquals("Novo Nome", atualizado.getNome());
        assertEquals(LocalDate.of(1999, 12, 31), atualizado.getDataNascimento());
        assertEquals("novo@email.com", atualizado.getEmail());
        assertEquals("Rua B", atualizado.getEndereco());
        assertEquals("11888888888", atualizado.getTelefone());
        assertEquals("senhaNovaHash", atualizado.getSenha());
    }

    @Test
    void atualizarUsuario_naoAtualizaComCamposBlank() {
        UsuarioUpdateRequest dto = new UsuarioUpdateRequest(
                "  ",
                null,
                " ",
                "   ",
                "",
                " "
        );

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioBase));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario atualizado = usuarioService.atualizarUsuario(1L, dto);

        assertEquals("Joao", atualizado.getNome());
        assertEquals("joao@email.com", atualizado.getEmail());
        assertEquals("Rua A", atualizado.getEndereco());
        assertEquals("11999999999", atualizado.getTelefone());
        assertEquals("senhaHash", atualizado.getSenha());
    }

    @Test
    void atualizarUsuario_lancaQuandoEmailJaExiste() {
        UsuarioUpdateRequest dto = new UsuarioUpdateRequest(
                null, null, "existente@email.com", null, null, null
        );

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioBase));
        when(usuarioRepository.findByEmail("existente@email.com")).thenReturn(Optional.of(usuarioBase));

        assertThrows(BadRequestException.class, () -> usuarioService.atualizarUsuario(1L, dto));
    }

    @Test
    void atualizarUsuario_lancaQuandoUsuarioNaoExiste() {
        UsuarioUpdateRequest dto = new UsuarioUpdateRequest(
                "Novo", null, null, null, null, null
        );

        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.atualizarUsuario(1L, dto));
    }

    @Test
    void deletarUsuario_lancaQuandoNaoExiste() {
        when(usuarioRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.deletarUsuario(1L));
        verify(usuarioRepository, never()).deleteById(anyLong());
    }

    @Test
    void deletarUsuario_removeQuandoExiste() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        usuarioService.deletarUsuario(1L);

        verify(usuarioRepository).deleteById(1L);
    }

    @Test
    void listarIngressosPorUsuario_lancaQuandoUsuarioNaoExiste() {
        when(usuarioRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.listarIngressosPorUsuario(1L));
    }

    @Test
    void listarIngressosPorUsuario_retornaIngressos() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        Ingresso ingresso = new Ingresso();
        Compra compra = new Compra();
        compra.setIngresso(ingresso);

        when(compraService.buscarComprasPorUsuario(1L)).thenReturn(List.of(compra));

        List<Ingresso> ingressos = usuarioService.listarIngressosPorUsuario(1L);

        assertEquals(1, ingressos.size());
        assertSame(ingresso, ingressos.get(0));
    }

    @Test
    void buscarUsuarioPorId_retornaOptional() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioBase));

        Optional<Usuario> result = usuarioService.buscarUsuarioPorId(1L);

        assertTrue(result.isPresent());
    }

    @Test
    void buscarUsuarioPorEmail_retornaOptional() {
        when(usuarioRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(usuarioBase));

        Optional<Usuario> result = usuarioService.buscarUsuarioPorEmail("joao@email.com");

        assertTrue(result.isPresent());
    }
}