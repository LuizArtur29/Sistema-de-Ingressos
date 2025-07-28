package com.vendaingressos.service;

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
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(usuarioId);
        Optional<Ingresso> ingressoOptional = ingressoRepository.findById(ingressoId);

        if (usuarioOptional.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado com ID: " + usuarioId);
        }
        if (ingressoOptional.isEmpty()) {
            throw new RuntimeException("Ingresso não encontrado com ID: " + ingressoId);
        }

        Usuario usuario = usuarioOptional.get();
        Ingresso ingresso = ingressoOptional.get();

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
        return compraRepository.findByUsuarioIdUsuario(usuarioId);
    }

    @Transactional
    public Compra atualizarStatusCompra(Long id, String novoStatus) {
        return compraRepository.findById(id).map(compra ->{
            compra.setStatus(novoStatus);
            return compraRepository.save(compra);
        }).orElseThrow(() -> new RuntimeException("Compra não encontrada com ID : " + id));
    }

    @Transactional
    public void deletarCompra(Long id) {
        compraRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public long contarIngressosVendidos(Long eventoId) { // Novo método
        List<Compra> comprasDoEvento = compraRepository.findByIngressoEventoId(eventoId);
        long totalIngressos = 0;
        for (Compra compra : comprasDoEvento) {
            totalIngressos += compra.getQuantidadeIngressos();
        }
        return totalIngressos;
    }
}