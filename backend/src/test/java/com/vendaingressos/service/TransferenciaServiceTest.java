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
    void deveTransferirQuandoValido() {
        TransferenciaRequest request = new TransferenciaRequest(10L, 2L, 50.0);

        when(ingressoService.buscarIngressoPorId(10L)).thenReturn(Optional.of(ingresso));
        when(ingressoService.isIngressoValido(10L)).thenReturn(true);
        when(usuarioService.buscarUsuarioPorId(2L)).thenReturn(Optional.of(comprador));
        when(transferenciaRepository.save(any(Transferencia.class))).thenAnswer(inv -> inv.getArgument(0));

        Transferencia transferencia = transferenciaService.realizarTransferencia(request, vendedor);

        assertEquals(comprador, ingresso.getCompra().getUsuario());
        assertEquals(vendedor, transferencia.getVendedor());
        assertEquals(comprador, transferencia.getComprador());
        assertEquals(50.0, transferencia.getValorRevenda());
        verify(compraRepository).save(compra);
        verify(transferenciaRepository).save(any(Transferencia.class));
    }

    @Test
    void deveFalharQuandoIngressoIdNulo() {
        TransferenciaRequest request = new TransferenciaRequest(null, 2L, 50.0);

        assertThrows(BadRequestException.class,
                () -> transferenciaService.realizarTransferencia(request, vendedor));
    }

    @Test
    void deveFalharQuandoIngressoNaoEncontrado() {
        TransferenciaRequest request = new TransferenciaRequest(10L, 2L, 50.0);
        when(ingressoService.buscarIngressoPorId(10L)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class,
                () -> transferenciaService.realizarTransferencia(request, vendedor));
    }

    @Test
    void deveFalharQuandoIngressoSemCompra() {
        TransferenciaRequest request = new TransferenciaRequest(10L, 2L, 50.0);

        Ingresso ingressoSemCompra = new Ingresso();
        ingressoSemCompra.setIdIngresso(10L);
        ingressoSemCompra.setCompra(null);

        when(ingressoService.buscarIngressoPorId(10L)).thenReturn(Optional.of(ingressoSemCompra));

        assertThrows(BadRequestException.class,
                () -> transferenciaService.realizarTransferencia(request, vendedor));
    }

    @Test
    void deveFalharQuandoNaoEhTitular() {
        TransferenciaRequest request = new TransferenciaRequest(10L, 2L, 50.0);

        Usuario outro = new Usuario();
        outro.setIdUsuario(99L);

        when(ingressoService.buscarIngressoPorId(10L)).thenReturn(Optional.of(ingresso));

        assertThrows(ForbiddenException.class,
                () -> transferenciaService.realizarTransferencia(request, outro));
    }

    @Test
    void deveFalharQuandoIngressoInvalido() {
        TransferenciaRequest request = new TransferenciaRequest(10L, 2L, 50.0);

        when(ingressoService.buscarIngressoPorId(10L)).thenReturn(Optional.of(ingresso));
        when(ingressoService.isIngressoValido(10L)).thenReturn(false);

        assertThrows(BadRequestException.class,
                () -> transferenciaService.realizarTransferencia(request, vendedor));
    }

    @Test
    void deveFalharQuandoCompradorNaoEncontrado() {
        TransferenciaRequest request = new TransferenciaRequest(10L, 2L, 50.0);

        when(ingressoService.buscarIngressoPorId(10L)).thenReturn(Optional.of(ingresso));
        when(ingressoService.isIngressoValido(10L)).thenReturn(true);
        when(usuarioService.buscarUsuarioPorId(2L)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class,
                () -> transferenciaService.realizarTransferencia(request, vendedor));
    }

    @Test
    void deveFalharQuandoCompradorIgualVendedor() {
        TransferenciaRequest request = new TransferenciaRequest(10L, 1L, 50.0);

        when(ingressoService.buscarIngressoPorId(10L)).thenReturn(Optional.of(ingresso));
        when(ingressoService.isIngressoValido(10L)).thenReturn(true);
        when(usuarioService.buscarUsuarioPorId(1L)).thenReturn(Optional.of(vendedor));

        assertThrows(BadRequestException.class,
                () -> transferenciaService.realizarTransferencia(request, vendedor));
    }

    @Test
    void deveFalharQuandoValorRevendaNulo() {
        TransferenciaRequest request = new TransferenciaRequest(10L, 2L, null);

        assertThrows(BadRequestException.class,
                () -> transferenciaService.realizarTransferencia(request, vendedor));
    }
}