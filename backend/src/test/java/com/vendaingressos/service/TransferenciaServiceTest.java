package com.vendaingressos.service;

import com.vendaingressos.dto.TransferenciaRequest;
import com.vendaingressos.exception.BadRequestException;
import com.vendaingressos.exception.ForbiddenException;
import com.vendaingressos.model.Compra;
import com.vendaingressos.model.Ingresso;
import com.vendaingressos.model.Transferencia;
import com.vendaingressos.model.Usuario;
import com.vendaingressos.repository.CompraRepository;
import com.vendaingressos.repository.TransferenciaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferenciaServiceTest {

    @Mock
    private TransferenciaRepository transferenciaRepository;

    @Mock
    private CompraRepository compraRepository;

    @Mock
    private IngressoService ingressoService;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private TransferenciaService transferenciaService;

    private Usuario vendedor;
    private Usuario comprador;
    private Ingresso ingresso;
    private Compra compra;

    @BeforeEach
    void setUp() {
        vendedor = new Usuario();
        vendedor.setIdUsuario(1L);

        comprador = new Usuario();
        comprador.setIdUsuario(2L);

        compra = new Compra();
        compra.setUsuario(vendedor);

        ingresso = new Ingresso();
        ingresso.setIdIngresso(10L);
        ingresso.setCompra(compra);
    }

    @Test
    @DisplayName("Deve realizar a transferência com sucesso quando todos os dados forem válidos")
    void deveRealizarTransferenciaComSucesso_QuandoDadosForemValidos() {
        // Arrange (Given)
        TransferenciaRequest request = new TransferenciaRequest(10L, 2L, 50.0);

        when(ingressoService.buscarIngressoPorId(10L)).thenReturn(Optional.of(ingresso));
        when(ingressoService.isIngressoValido(10L)).thenReturn(true);
        when(usuarioService.buscarUsuarioPorId(2L)).thenReturn(Optional.of(comprador));
        when(transferenciaRepository.save(any(Transferencia.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act (When)
        Transferencia transferencia = transferenciaService.realizarTransferencia(request, vendedor);

        // Assert (Then)
        assertAll("Validações de sucesso na transferência",
                () -> assertEquals(comprador, ingresso.getCompra().getUsuario(), "O comprador deve se tornar o novo titular da compra do ingresso"),
                () -> assertEquals(vendedor, transferencia.getVendedor(), "O vendedor deve ser associado corretamente à transferência"),
                () -> assertEquals(comprador, transferencia.getComprador(), "O comprador deve ser associado corretamente à transferência"),
                () -> assertEquals(50.0, transferencia.getValorRevenda(), "O valor de revenda deve corresponder ao solicitado")
        );

        verify(compraRepository, times(1)).save(compra);
        verify(transferenciaRepository, times(1)).save(any(Transferencia.class));
    }

    @Test
    @DisplayName("Deve lançar BadRequestException quando o ID do ingresso fornecido for nulo")
    void deveLancarBadRequestException_QuandoIdDoIngressoForNulo() {
        // Arrange (Given)
        TransferenciaRequest request = new TransferenciaRequest(null, 2L, 50.0);

        // Act & Assert (When & Then)
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> transferenciaService.realizarTransferencia(request, vendedor));
        assertEquals("Ingresso inválido para transferência.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar BadRequestException quando o ingresso não for encontrado no sistema")
    void deveLancarBadRequestException_QuandoIngressoNaoForEncontrado() {
        // Arrange (Given)
        TransferenciaRequest request = new TransferenciaRequest(10L, 2L, 50.0);
        when(ingressoService.buscarIngressoPorId(10L)).thenReturn(Optional.empty());

        // Act & Assert (When & Then)
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> transferenciaService.realizarTransferencia(request, vendedor));
        assertEquals("Ingresso não encontrado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar BadRequestException quando o ingresso informado não possuir uma compra vinculada")
    void deveLancarBadRequestException_QuandoIngressoNaoPossuirCompraVinculada() {
        // Arrange (Given)
        TransferenciaRequest request = new TransferenciaRequest(10L, 2L, 50.0);
        Ingresso ingressoSemCompra = new Ingresso();
        ingressoSemCompra.setIdIngresso(10L);
        ingressoSemCompra.setCompra(null);

        when(ingressoService.buscarIngressoPorId(10L)).thenReturn(Optional.of(ingressoSemCompra));

        // Act & Assert (When & Then)
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> transferenciaService.realizarTransferencia(request, vendedor));
        assertEquals("Ingresso sem titular válido para transferência.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar ForbiddenException quando o usuário que tenta transferir não for o titular do ingresso")
    void deveLancarForbiddenException_QuandoUsuarioSolicitanteNaoForOTitular() {
        // Arrange (Given)
        TransferenciaRequest request = new TransferenciaRequest(10L, 2L, 50.0);
        Usuario outroUsuario = new Usuario();
        outroUsuario.setIdUsuario(99L);

        when(ingressoService.buscarIngressoPorId(10L)).thenReturn(Optional.of(ingresso));

        // Act & Assert (When & Then)
        ForbiddenException exception = assertThrows(ForbiddenException.class,
                () -> transferenciaService.realizarTransferencia(request, outroUsuario));
        assertEquals("Apenas o titular atual do ingresso pode transferir.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar BadRequestException quando o ingresso for inválido ou já tiver sido utilizado")
    void deveLancarBadRequestException_QuandoIngressoForInvalidoOuJaUtilizado() {
        // Arrange (Given)
        TransferenciaRequest request = new TransferenciaRequest(10L, 2L, 50.0);

        when(ingressoService.buscarIngressoPorId(10L)).thenReturn(Optional.of(ingresso));
        when(ingressoService.isIngressoValido(10L)).thenReturn(false);

        // Act & Assert (When & Then)
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> transferenciaService.realizarTransferencia(request, vendedor));
        assertEquals("Ingresso inválido ou já utilizado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar BadRequestException quando o comprador destino não for encontrado")
    void deveLancarBadRequestException_QuandoCompradorNaoForEncontrado() {
        // Arrange (Given)
        TransferenciaRequest request = new TransferenciaRequest(10L, 2L, 50.0);

        when(ingressoService.buscarIngressoPorId(10L)).thenReturn(Optional.of(ingresso));
        when(ingressoService.isIngressoValido(10L)).thenReturn(true);
        when(usuarioService.buscarUsuarioPorId(2L)).thenReturn(Optional.empty());

        // Act & Assert (When & Then)
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> transferenciaService.realizarTransferencia(request, vendedor));
        assertEquals("Comprador não encontrado.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar BadRequestException quando o comprador e o vendedor forem o mesmo usuário")
    void deveLancarBadRequestException_QuandoCompradorForOMesmoQueOVendedor() {
        // Arrange (Given)
        TransferenciaRequest request = new TransferenciaRequest(10L, 1L, 50.0);

        when(ingressoService.buscarIngressoPorId(10L)).thenReturn(Optional.of(ingresso));
        when(ingressoService.isIngressoValido(10L)).thenReturn(true);
        when(usuarioService.buscarUsuarioPorId(1L)).thenReturn(Optional.of(vendedor));

        // Act & Assert (When & Then)
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> transferenciaService.realizarTransferencia(request, vendedor));
        assertEquals("Comprador não pode ser o mesmo usuário que o vendedor.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar BadRequestException quando o valor da revenda não for informado")
    void deveLancarBadRequestException_QuandoValorDeRevendaForNulo() {
        // Arrange (Given)
        TransferenciaRequest request = new TransferenciaRequest(10L, 2L, null);

        // Act & Assert (When & Then)
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> transferenciaService.realizarTransferencia(request, vendedor));
        assertEquals("valorRevenda é obrigatório.", exception.getMessage());
    }
}