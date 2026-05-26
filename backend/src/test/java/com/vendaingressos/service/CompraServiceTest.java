package com.vendaingressos.service;

import com.vendaingressos.exception.BadRequestException;
import com.vendaingressos.model.*;
import com.vendaingressos.model.enums.MetodoPagamento;
import com.vendaingressos.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompraServiceTest {

    @Mock private CompraRepository compraRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private IngressoRepository ingressoRepository;
    @Mock private SessaoEventoRepository sessaoEventoRepository;
    @Mock private TipoIngressoRepository tipoIngressoRepository;

    @InjectMocks
    private CompraService compraService;

    private Usuario usuarioJovem;
    private Usuario usuarioAdulto;
    private Ingresso ingresso;
    private SessaoEvento sessao;
    private TipoIngresso tipoIngresso;

    @BeforeEach
    void setUp() {
        usuarioJovem = new Usuario();
        usuarioJovem.setIdUsuario(1L);
        usuarioJovem.setDataNascimento(LocalDate.now().minusYears(15)); // 15 anos

        usuarioAdulto = new Usuario();
        usuarioAdulto.setIdUsuario(2L);
        usuarioAdulto.setDataNascimento(LocalDate.now().minusYears(25)); // 25 anos

        sessao = new SessaoEvento();
        sessao.setIdSessao(10L);
        sessao.setCapacidade(100);

        tipoIngresso = new TipoIngresso();
        tipoIngresso.setIdTipoIngresso(20L);
        tipoIngresso.setQuantidadeDisponivel(50);

        ingresso = new Ingresso();
        ingresso.setIdIngresso(100L);
        ingresso.setIngressoDisponivel(true);
        ingresso.setVendido(false);
        ingresso.setPreco(100.0);
        ingresso.setSessaoEvento(sessao);
        ingresso.setTipoIngresso(tipoIngresso);
    }

    @Test
    @DisplayName("Deve realizar compra com sucesso para ingresso inteira")
    void deveRealizarCompraComSucesso_QuandoDadosValidos() {
        // Given
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuarioAdulto));
        when(ingressoRepository.findByIdForUpdate(100L)).thenReturn(Optional.of(ingresso));
        when(sessaoEventoRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(sessao));
        when(ingressoRepository.countBySessaoEventoIdSessaoAndVendidoTrue(10L)).thenReturn(0L);
        when(ingressoRepository.countBySessaoEventoIdSessaoAndTipoIngressoIdTipoIngressoAndIngressoDisponivelTrueAndVendidoFalse(10L, 20L))
                .thenReturn(10L);
        when(tipoIngressoRepository.findByIdForUpdate(20L)).thenReturn(Optional.of(tipoIngresso));

        Compra compraSalva = new Compra();
        compraSalva.setIdCompra(1L);
        compraSalva.setValorTotal(100.0);
        when(compraRepository.save(any(Compra.class))).thenReturn(compraSalva);
        when(compraRepository.findByIdFetchIngressos(1L)).thenReturn(Optional.of(compraSalva));

        Compra resultado = compraService.realizarCompra(2L, 100L, 1, MetodoPagamento.PIX, false);

        assertNotNull(resultado);
        assertEquals(100.0, resultado.getValorTotal());
        verify(compraRepository, times(1)).save(any(Compra.class));
        verify(ingressoRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("Deve lançar BadRequestException quando a quantidade de ingressos for menor ou igual a zero")
    void deveLancarBadRequestException_QuandoQuantidadeInvalida() {
        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                compraService.realizarCompra(1L, 100L, 0, MetodoPagamento.CARTAO_CREDITO, false)
        );
        assertEquals("A quantidade de ingressos deve ser maior que zero.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve aplicar desconto de meia-entrada se usuário tiver menos de 18 anos")
    void deveAplicarMeiaEntrada_QuandoUsuarioForMenorDeIdade() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioJovem));
        when(ingressoRepository.findByIdForUpdate(100L)).thenReturn(Optional.of(ingresso));
        when(sessaoEventoRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(sessao));
        when(ingressoRepository.countBySessaoEventoIdSessaoAndTipoIngressoIdTipoIngressoAndIngressoDisponivelTrueAndVendidoFalse(10L, 20L))
                .thenReturn(10L);
        when(tipoIngressoRepository.findByIdForUpdate(20L)).thenReturn(Optional.of(tipoIngresso));

        Compra compraSalva = new Compra();
        compraSalva.setIdCompra(2L);
        compraSalva.setValorTotal(50.0); // 100.0 / 2
        when(compraRepository.save(any(Compra.class))).thenReturn(compraSalva);
        when(compraRepository.findByIdFetchIngressos(2L)).thenReturn(Optional.of(compraSalva));

        Compra resultado = compraService.realizarCompra(1L, 100L, 1, MetodoPagamento.PIX, true);

        assertEquals(50.0, resultado.getValorTotal());
    }

    @Test
    @DisplayName("Deve lançar BadRequestException quando maior de idade solicitar meia-entrada por idade")
    void deveLancarException_QuandoAdultoSolicitarMeiaEntrada() {
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuarioAdulto));
        when(ingressoRepository.findByIdForUpdate(100L)).thenReturn(Optional.of(ingresso));
        when(sessaoEventoRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(sessao));
        when(ingressoRepository.countBySessaoEventoIdSessaoAndTipoIngressoIdTipoIngressoAndIngressoDisponivelTrueAndVendidoFalse(10L, 20L))
                .thenReturn(10L);
        when(tipoIngressoRepository.findByIdForUpdate(20L)).thenReturn(Optional.of(tipoIngresso));

        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                compraService.realizarCompra(2L, 100L, 1, MetodoPagamento.PIX, true)
        );
        assertEquals("O usuário não tem direito à meia-entrada por idade.", exception.getMessage());
    }
}