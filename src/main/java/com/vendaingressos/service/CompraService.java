package com.vendaingressos.service;

import com.vendaingressos.exception.BadRequestException;
import com.vendaingressos.exception.ResourceNotFoundException;
import com.vendaingressos.model.Compra;
import com.vendaingressos.model.Ingresso;
import com.vendaingressos.model.Usuario;
import com.vendaingressos.repository.CompraRepository;
import com.vendaingressos.repository.IngressoRepository;
import com.vendaingressos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CompraService {

    private final CompraRepository compraRepository;
    private final UsuarioRepository usuarioRepository;
    private final IngressoRepository ingressoRepository;

    @Autowired
    public CompraService(CompraRepository compraRepository, UsuarioRepository usuarioRepository, IngressoRepository ingressoRepository) {
        this.compraRepository = compraRepository;
        this.usuarioRepository = usuarioRepository;
        this.ingressoRepository = ingressoRepository;
    }

    @Transactional
    public Compra realizarCompra(Long usuarioId, Long ingressoId, int quantidadeIngressos, String metodoPagamento, boolean isMeiaEntrada ) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + usuarioId));
        Ingresso ingresso = ingressoRepository.findById(ingressoId)
                .orElseThrow(() -> new ResourceNotFoundException("Ingresso não encontrado com ID: " + ingressoId));

        if (!ingresso.isIngressoDisponivel()) {
            throw new BadRequestException("Ingresso não está disponível para compra.");
        }
        if (quantidadeIngressos <= 0) {
            throw new BadRequestException("A quantidade de ingressos deve ser maior que zero.");
        }

        // Adicionar uma verificação mais robusta de disponibilidade baseada na capacidade da sessão
        // e nos ingressos já vendidos para aquela sessão.
        // A capacidade é do Evento pai, mas a contagem de vendidos é por SessaoEvento.
        Integer capacidadeTotalEventoPorDia = ingresso.getSessaoEvento().getEventoPai().getCapacidadeTotal();
        long ingressosVendidosNestaSessao = contarIngressosVendidosPorSessao(ingresso.getSessaoEvento().getIdSessao());

        if ((ingressosVendidosNestaSessao + quantidadeIngressos) > capacidadeTotalEventoPorDia) {
            throw new BadRequestException("Não há ingressos suficientes disponíveis para esta sessão do evento.");
        }


        double precoUnitarioBase = ingresso.getPreco();

        double precoFinalUnitario = precoUnitarioBase;
        if (isMeiaEntrada) {
            precoFinalUnitario = precoUnitarioBase / 2.0;
        }

        double valorTotal = precoFinalUnitario * quantidadeIngressos;

        Compra novaCompra = new Compra();
        novaCompra.setUsuario(usuario);
        novaCompra.setIngresso(ingresso);
        novaCompra.setDataCompra(LocalDate.now());
        novaCompra.setQuantidadeIngressos(quantidadeIngressos);
        novaCompra.setValorTotal(valorTotal);
        novaCompra.setMetodoPagamento(metodoPagamento);
        novaCompra.setStatus("Concluida");

        return compraRepository.save(novaCompra);
    }

    @Transactional(readOnly = true)
    public List<Compra> buscarTodasCompras() {
        return compraRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Compra> buscarCompraPorId(Long id) {
        return compraRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Compra> buscarComprasPorUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuário não encontrado com ID: " + usuarioId);
        }
        return compraRepository.findByUsuarioIdUsuario(usuarioId);
    }

    @Transactional
    public Compra atualizarStatusCompra(Long id, String novoStatus) {
        return compraRepository.findById(id).map(compra ->{
            compra.setStatus(novoStatus);
            return compraRepository.save(compra);
        }).orElseThrow(() -> new ResourceNotFoundException("Compra não encontrada com ID : " + id));
    }

    @Transactional
    public void deletarCompra(Long id) {
        if (!compraRepository.existsById(id)) {
            throw new RuntimeException("Compra não encontrada com ID: " + id);
        }
        compraRepository.deleteById(id);
    }

    // Novo método para contar ingressos vendidos por Sessão de Evento
    @Transactional(readOnly = true)
    public long contarIngressosVendidosPorSessao(Long sessaoEventoId) {
        List<Compra> comprasDaSessao = compraRepository.findByIngressoSessaoEventoIdSessao(sessaoEventoId);
        long totalIngressos = 0;
        for (Compra compra : comprasDaSessao) {
            totalIngressos += compra.getQuantidadeIngressos();
        }
        return totalIngressos;
    }
}