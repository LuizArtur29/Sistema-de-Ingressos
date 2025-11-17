package com.vendaingressos.service;

import com.vendaingressos.exception.BadRequestException;
import com.vendaingressos.exception.ResourceNotFoundException;
import com.vendaingressos.model.Compra;
import com.vendaingressos.model.Ingresso;
import com.vendaingressos.model.Usuario;
import com.vendaingressos.model.enums.MetodoPagamento;
import com.vendaingressos.redis.CompraRedisCache;
import com.vendaingressos.repository.CompraRepository;
import com.vendaingressos.repository.IngressoRepository;
import com.vendaingressos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@Service
public class CompraService {

    private final CompraRepository compraRepository;
    private final UsuarioRepository usuarioRepository;
    private final IngressoRepository ingressoRepository;
    private final CompraRedisCache compraRedisCache;

    @Autowired
    public CompraService(CompraRepository compraRepository, UsuarioRepository usuarioRepository, IngressoRepository ingressoRepository, CompraRedisCache compraRedisCache) {
        this.compraRepository = compraRepository;
        this.usuarioRepository = usuarioRepository;
        this.ingressoRepository = ingressoRepository;
        this.compraRedisCache = compraRedisCache;
    }

    @Transactional
    public Compra realizarCompra(Long usuarioId, Long ingressoId, int quantidadeIngressos, MetodoPagamento metodoPagamento, boolean isMeiaEntrada ) {
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
            LocalDate hoje = LocalDate.now();
            int idade = Period.between(usuario.getDataNascimento(), hoje).getYears();
            if (idade >= 18) {
                throw new BadRequestException("O usuário não tem direito a meia-entrada por idade.");
            }
            precoFinalUnitario = precoUnitarioBase / 2.0;
        }

        double valorTotal = precoFinalUnitario * quantidadeIngressos;

        Compra novaCompra = new Compra();
        novaCompra.setUsuario(usuario);
        novaCompra.setIngresso(ingresso);
        novaCompra.setDataCompra(LocalDate.now());
        novaCompra.setQuantidadeIngressos(quantidadeIngressos);
        novaCompra.setValorTotal(valorTotal);
        novaCompra.setMetodoPagamento(metodoPagamento.name());
        novaCompra.setStatus("Concluida");

        Compra compraSalva = compraRepository.save(novaCompra);

        compraRedisCache.cacheCompra(compraSalva);
        compraRedisCache.invalidateCacheForComprasPorUsuario(usuarioId);

        return compraSalva;
    }

    @Transactional(readOnly = true)
    public List<Compra> buscarTodasCompras() {
        return compraRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Compra> buscarCompraPorId(Long id) {
        Optional<Compra> cachedCompra = compraRedisCache.getCachedCompra(id);
        return compraRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Compra> buscarComprasPorUsuario(Long usuarioId) {
        // 1. Tenta buscar a lista no cache
        Optional<List<Compra>> cachedList = compraRedisCache.getCachedComprasPorUsuario(usuarioId);
        if (cachedList.isPresent()) {
            return cachedList.get();
        }

        // 2. Verifica a existência do usuário e busca no banco
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuário não encontrado com ID: " + usuarioId);
        }
        List<Compra> compras = compraRepository.findByUsuarioIdUsuario(usuarioId);

        // 3. Se encontrado, armazena a lista no cache
        if (!compras.isEmpty()) {
            compraRedisCache.cacheComprasPorUsuario(usuarioId, compras);
        }
        return compras;
    }

    @Transactional
    public Compra atualizarStatusCompra(Long id, String novoStatus) {
        return compraRepository.findById(id).map(compra ->{
            compra.setStatus(novoStatus);
            Compra compraSalva = compraRepository.save(compra);

            // NOVO: Invalida o item único e a lista do usuário
            compraRedisCache.invalidateCacheForCompra(id);
            compraRedisCache.invalidateCacheForComprasPorUsuario(compra.getUsuario().getIdUsuario());
            compraRedisCache.cacheCompra(compraSalva); // Recria o cache do item único

            return compraSalva;
        }).orElseThrow(() -> new ResourceNotFoundException("Compra não encontrada com ID : " + id));
    }

    @Transactional
    public void deletarCompra(Long id) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compra não encontrada com ID: " + id));

        Long usuarioId = compra.getUsuario().getIdUsuario();

        compraRepository.deleteById(id);

        // NOVO: Invalida o item único e a lista do usuário
        compraRedisCache.invalidateCacheForCompra(id);
        compraRedisCache.invalidateCacheForComprasPorUsuario(usuarioId);
    }

    // Novo método para contar ingressos vendidos por Sessão de Evento
    @Transactional(readOnly = true)
    public long contarIngressosVendidosPorSessao(Long sessaoEventoId) {
        Long totalIngressos = compraRepository.sumQuantidadeIngressosByIngressoSessaoEventoIdSessao(sessaoEventoId);
        return totalIngressos != null ? totalIngressos : 0;
    }
}