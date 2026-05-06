package com.vendaingressos.service;

import com.vendaingressos.dto.TransferenciaRequest;
import com.vendaingressos.exception.BadRequestException;
import com.vendaingressos.exception.ForbiddenException;
import com.vendaingressos.model.Ingresso;
import com.vendaingressos.model.Transferencia;
import com.vendaingressos.model.Usuario;
import com.vendaingressos.repository.CompraRepository;
import com.vendaingressos.repository.TransferenciaRepository; // Necessário criar a interface JpaRepository
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferenciaService {

    private final TransferenciaRepository transferenciaRepository;

    private final CompraRepository compraRepository;

    private final IngressoService ingressoService;

    private static final Logger log = LoggerFactory.getLogger(TransferenciaService.class);
    private final UsuarioService usuarioService;

    public TransferenciaService(TransferenciaRepository transferenciaRepository, CompraRepository compraRepository, IngressoService ingressoService, UsuarioService usuarioService) {
        this.transferenciaRepository = transferenciaRepository;
        this.compraRepository = compraRepository;
        this.ingressoService = ingressoService;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public Transferencia realizarTransferencia(TransferenciaRequest request, Usuario vendedor) {
        if (request.ingressoId() == null) {
            throw new BadRequestException("Ingresso inválido para transferência.");
        }

        if (request.valorRevenda() == null) {
            throw new BadRequestException("valorRevenda é obrigatório.");
        }

        Ingresso ingresso = ingressoService.buscarIngressoPorId(request.ingressoId())
                .orElseThrow(() -> new BadRequestException("Ingresso não encontrado."));

        if (ingresso.getCompra() == null || ingresso.getCompra().getUsuario() == null) {
            throw new BadRequestException("Ingresso sem titular válido para transferência.");
        }

        Long titularAtualId = ingresso.getCompra().getUsuario().getIdUsuario();

        if (vendedor == null || vendedor.getIdUsuario() == null || !vendedor.getIdUsuario().equals(titularAtualId)) {
            throw new ForbiddenException("Apenas o titular atual do ingresso pode transferir.");
        }

        if (!ingressoService.isIngressoValido(ingresso.getIdIngresso())) {
            throw new BadRequestException("Ingresso inválido ou já utilizado.");
        }

        Usuario comprador = usuarioService.buscarUsuarioPorId(request.compradorId())
                .orElseThrow(() -> new BadRequestException("Comprador não encontrado."));

        if (comprador.getIdUsuario().equals(vendedor.getIdUsuario())) {
            throw new BadRequestException("Comprador não pode ser o mesmo usuário que o vendedor.");
        }

        log.info(
                "Transferencia: ingressoId={}, vendedorId={}, compradorId={}, valorRevenda={}",
                ingresso.getIdIngresso(),
                vendedor.getIdUsuario(),
                comprador.getIdUsuario(),
                request.valorRevenda()
        );

        // Atualiza dono do ingresso
        ingresso.getCompra().setUsuario(comprador);

        Transferencia transferencia = new Transferencia();
        transferencia.setIngressoTransferido(ingresso);
        transferencia.setVendedor(vendedor);
        transferencia.setComprador(comprador);
        transferencia.setValorRevenda(request.valorRevenda());

        compraRepository.save(ingresso.getCompra());

        return transferenciaRepository.save(transferencia);
    }
}