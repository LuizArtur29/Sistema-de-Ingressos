package com.vendaingressos.service;

import com.vendaingressos.exception.BadRequestException;
import com.vendaingressos.exception.ResourceNotFoundException;
import com.vendaingressos.model.Compra;
import com.vendaingressos.model.Ingresso;
import com.vendaingressos.model.Usuario;
import com.vendaingressos.model.enums.MetodoPagamento;
import com.vendaingressos.redis.CompraRedisCache;
import com.vendaingressos.repository.jdbc.CompraRepository;
import com.vendaingressos.repository.jdbc.IngressoRepository;
import com.vendaingressos.repository.jdbc.UsuarioRepository;
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

        Usuario usuario = usuarioRepository.buscarPorId(usuarioId);
        if (usuario == null) {
            new ResourceNotFoundException("Usuário não encontrado com ID: " + usuarioId);
        }

        Ingresso ingresso = ingressoRepository.buscarPorId(ingressoId);
        if (ingresso == null) {
            new ResourceNotFoundException("Ingresso não encontrado com ID: " + ingressoId);
        }

        if (!ingresso.isIngressoDisponivel()) {
            throw new BadRequestException("Ingresso não está disponível para compra.");
        }
        if (quantidadeIngressos <= 0) {
            throw new BadRequestException("A quantidade de ingressos deve ser maior que zero.");
        }

        if (ingresso.getSessaoEvento() == null || ingresso.getSessaoEvento().getEventoPai() == null) {
            throw new RuntimeException("Erro interno: Dados do evento não foram carregados corretamente pelo banco.");
        }

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
            if (usuario.getDataNascimento() == null) {
                throw new BadRequestException("Data de nascimento do usuário não cadastrada.");
            }
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

        compraRepository.salvar(novaCompra);

        compraRedisCache.cacheCompra(novaCompra);
        compraRedisCache.invalidateCacheForComprasPorUsuario(usuarioId);

        return novaCompra;
    }

    @Transactional(readOnly = true)
    public List<Compra> buscarTodasCompras() {
        return compraRepository.listarTodos();
    }

    @Transactional(readOnly = true)
    public Optional<Compra> buscarCompraPorId(Long id) {
        Optional<Compra> cachedCompra = compraRedisCache.getCachedCompra(id);
        if (cachedCompra.isPresent()) {
            return cachedCompra;
        }

        Compra compra = compraRepository.buscarPorId(id);
        return Optional.ofNullable(compra);
    }

    @Transactional(readOnly = true)
    public List<Compra> buscarComprasPorUsuario(Long usuarioId) {
        // 1. Tenta buscar a lista no cache
        Optional<List<Compra>> cachedList = compraRedisCache.getCachedComprasPorUsuario(usuarioId);
        if (cachedList.isPresent()) {
            return cachedList.get();
        }

        // 2. Verifica a existência do usuário e busca no banco
        if (usuarioRepository.buscarPorId(usuarioId) == null) {
            throw new ResourceNotFoundException("Usuário não encontrado com ID: " + usuarioId);
        }
        List<Compra> compras = compraRepository.buscarPorUsuario(usuarioId);

        // 3. Se encontrado, armazena a lista no cache
        if (!compras.isEmpty()) {
            compraRedisCache.cacheComprasPorUsuario(usuarioId, compras);
        }
        return compras;
    }

    @Transactional
    public Compra atualizarStatusCompra(Long id, String novoStatus) {
        Compra compra = compraRepository.buscarPorId(id);

        if (compra == null) {
            throw new ResourceNotFoundException("Compra não encontrada com ID : " + id);
        }

        // Atualiza apenas o status no banco (Performance)
        compra.setStatus(novoStatus);
        compraRepository.atualizarStatus(id, novoStatus);

        // Invalida cache
        compraRedisCache.invalidateCacheForCompra(id);
        if (compra.getUsuario() != null) {
            compraRedisCache.invalidateCacheForComprasPorUsuario(compra.getUsuario().getIdUsuario());
        }

        // Recria cache unitário
        compraRedisCache.cacheCompra(compra);

        return compra;
    }

    @Transactional
    public void deletarCompra(Long id) {
        Compra compra = compraRepository.buscarPorId(id);
        if (compra == null) {
            throw new RuntimeException("Compra não encontrada com ID: " + id);
        }

        Long usuarioId = compra.getUsuario().getIdUsuario();

        compraRepository.deletar(id);

        // NOVO: Invalida o item único e a lista do usuário
        compraRedisCache.invalidateCacheForCompra(id);
        compraRedisCache.invalidateCacheForComprasPorUsuario(usuarioId);
    }

    // Novo método para contar ingressos vendidos por Sessão de Evento
    @Transactional(readOnly = true)
    public long contarIngressosVendidosPorSessao(Long sessaoEventoId) {
        return compraRepository.contarIngressosVendidosPorSessao(sessaoEventoId);
    }
}