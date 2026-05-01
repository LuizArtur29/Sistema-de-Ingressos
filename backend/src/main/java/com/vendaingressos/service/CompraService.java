package com.vendaingressos.service;

import com.vendaingressos.dto.StatusUpdateRequest;
import com.vendaingressos.exception.BadRequestException;
import com.vendaingressos.exception.ResourceNotFoundException;
import com.vendaingressos.model.Compra;
import com.vendaingressos.model.Ingresso;
import com.vendaingressos.model.SessaoEvento;
import com.vendaingressos.model.TipoIngresso;
import com.vendaingressos.model.Usuario;
import com.vendaingressos.model.enums.MetodoPagamento;
import com.vendaingressos.repository.CompraRepository;
import com.vendaingressos.repository.IngressoRepository;
import com.vendaingressos.repository.SessaoEventoRepository;
import com.vendaingressos.repository.TipoIngressoRepository;
import com.vendaingressos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CompraService {

    private final CompraRepository compraRepository;
    private final UsuarioRepository usuarioRepository;
    private final IngressoRepository ingressoRepository;
    private final SessaoEventoRepository sessaoEventoRepository;
    private final TipoIngressoRepository tipoIngressoRepository;

    @Autowired
    public CompraService(CompraRepository compraRepository,
                         UsuarioRepository usuarioRepository,
                         IngressoRepository ingressoRepository,
                         SessaoEventoRepository sessaoEventoRepository,
                         TipoIngressoRepository tipoIngressoRepository) {
        this.compraRepository = compraRepository;
        this.usuarioRepository = usuarioRepository;
        this.ingressoRepository = ingressoRepository;
        this.sessaoEventoRepository = sessaoEventoRepository;
        this.tipoIngressoRepository = tipoIngressoRepository;
    }

    @Transactional
    public Compra realizarCompra(Long usuarioId, Long ingressoId, int quantidadeIngressos, MetodoPagamento metodoPagamento, boolean isMeiaEntrada) {
        if (quantidadeIngressos <= 0) {
            throw new BadRequestException("A quantidade de ingressos deve ser maior que zero.");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + usuarioId));

        Ingresso ingressoBase = ingressoRepository.findByIdForUpdate(ingressoId)
                .orElseThrow(() -> new ResourceNotFoundException("Ingresso não encontrado com ID: " + ingressoId));

        if (!ingressoBase.isIngressoDisponivel()) {
            throw new BadRequestException("Ingresso indisponível para compra.");
        }
        if (ingressoBase.isVendido()) {
            throw new BadRequestException("O ingresso informado já foi vendido.");
        }
        if (ingressoBase.getTipoIngresso() == null) {
            throw new BadRequestException("Ingresso sem tipo associado não pode ser comercializado.");
        }

        Long sessaoEventoId = ingressoBase.getSessaoEvento().getIdSessao();
        Long tipoIngressoId = ingressoBase.getTipoIngresso().getIdTipoIngresso();
        SessaoEvento sessaoBloqueada = sessaoEventoRepository.findByIdForUpdate(sessaoEventoId)
                .orElseThrow(() -> new ResourceNotFoundException("Sessão não encontrada com ID: " + sessaoEventoId));

        int capacidadeMaximaSessao = resolverCapacidadeMaximaSessao(sessaoBloqueada);

        long ingressosVendidosNestaSessao = contarIngressosVendidosPorSessao(sessaoEventoId);
        if ((ingressosVendidosNestaSessao + quantidadeIngressos) > capacidadeMaximaSessao) {
            throw new BadRequestException("Não há ingressos suficientes disponíveis para esta sessão do evento.");
        }

        long ingressosDisponiveisParaTipo = ingressoRepository
                .countBySessaoEventoIdSessaoAndTipoIngressoIdTipoIngressoAndIngressoDisponivelTrueAndVendidoFalse(
                        sessaoEventoId,
                        tipoIngressoId
                );
        if (ingressosDisponiveisParaTipo < quantidadeIngressos) {
            throw new BadRequestException("Quantidade solicitada indisponível para o tipo de ingresso selecionado.");
        }

        List<Ingresso> ingressosReservados = reservarIngressosIncluindoSemente(
                ingressoBase,
                sessaoEventoId,
                tipoIngressoId,
                quantidadeIngressos
        );

        if (ingressosReservados.size() != quantidadeIngressos) {
            throw new BadRequestException("Não foi possível reservar a quantidade solicitada. Tente novamente.");
        }

        aplicarDecrementoTipoIngresso(tipoIngressoId, ingressosReservados.size());

        double precoUnitarioBase = ingressoBase.getPreco();

        double precoFinalUnitario = precoUnitarioBase;
        if (isMeiaEntrada) {
            LocalDate hoje = LocalDate.now();
            int idade = Period.between(usuario.getDataNascimento(), hoje).getYears();
            if (idade >= 18) {
                throw new BadRequestException("O usuário não tem direito a meia-entrada por idade.");
            }
            precoFinalUnitario = precoUnitarioBase / 2.0;
        }

        double valorTotal = precoFinalUnitario * ingressosReservados.size();

        Compra novaCompra = new Compra();
        novaCompra.setUsuario(usuario);
        novaCompra.setDataCompra(LocalDate.now());
        novaCompra.setQuantidadeIngressos(ingressosReservados.size());
        novaCompra.setValorTotal(valorTotal);
        novaCompra.setMetodoPagamento(metodoPagamento.name());
        novaCompra.setStatus("Concluida");

        Compra compraSalva = compraRepository.save(novaCompra);

        for (Ingresso ingressoReservado : ingressosReservados) {
            ingressoReservado.setVendido(true);
            ingressoReservado.setCompra(compraSalva);
        }
        ingressoRepository.saveAll(ingressosReservados);

        compraRepository.flush();
        return compraRepository.findByIdFetchIngressos(compraSalva.getIdCompra())
                .orElse(compraSalva);
    }

    /**
     * Reserva exatamente {@code quantidade} ingressos, sempre incluindo o ingresso indicado pelo cliente.
     */
    private List<Ingresso> reservarIngressosIncluindoSemente(
            Ingresso ingressoBase,
            Long sessaoEventoId,
            Long tipoIngressoId,
            int quantidade
    ) {
        if (quantidade == 1) {
            return List.of(ingressoBase);
        }

        List<Ingresso> demais = ingressoRepository.findDisponiveisParaVendaComLockExcluindo(
                sessaoEventoId,
                tipoIngressoId,
                ingressoBase.getIdIngresso(),
                PageRequest.of(0, quantidade - 1)
        );
        if (demais.size() != quantidade - 1) {
            throw new BadRequestException("Não foi possível reservar a quantidade solicitada. Tente novamente.");
        }

        List<Ingresso> todos = new ArrayList<>(quantidade);
        todos.add(ingressoBase);
        todos.addAll(demais);
        return todos;
    }

    private int resolverCapacidadeMaximaSessao(SessaoEvento sessao) {
        Integer capSessao = sessao.getCapacidade();
        if (capSessao != null && capSessao > 0) {
            return capSessao;
        }
        Integer capEvento = sessao.getEventoPai().getCapacidadeTotal();
        if (capEvento == null || capEvento <= 0) {
            throw new BadRequestException("Capacidade da sessão inválida para realizar compra.");
        }
        return capEvento;
    }

    private void aplicarDecrementoTipoIngresso(Long tipoIngressoId, int quantidade) {
        TipoIngresso tipo = tipoIngressoRepository.findByIdForUpdate(tipoIngressoId)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de ingresso não encontrado"));

        Integer disp = tipo.getQuantidadeDisponivel();
        if (disp != null) {
            if (disp < quantidade) {
                throw new BadRequestException("Quantidade solicitada indisponível para o tipo de ingresso selecionado.");
            }
            tipo.setQuantidadeDisponivel(disp - quantidade);
            tipoIngressoRepository.save(tipo);
        }
    }

    private void restaurarQuantidadeTipoIngressoPorIngressos(List<Ingresso> ingressos) {
        Map<Long, Integer> porTipo = new HashMap<>();
        for (Ingresso ing : ingressos) {
            if (ing.getTipoIngresso() == null) {
                continue;
            }
            Long tid = ing.getTipoIngresso().getIdTipoIngresso();
            porTipo.merge(tid, 1, Integer::sum);
        }
        for (Map.Entry<Long, Integer> e : porTipo.entrySet()) {
            TipoIngresso tipo = tipoIngressoRepository.findById(e.getKey())
                    .orElseThrow(() -> new ResourceNotFoundException("Tipo de ingresso não encontrado"));
            Integer disp = tipo.getQuantidadeDisponivel();
            if (disp != null) {
                tipo.setQuantidadeDisponivel(disp + e.getValue());
                tipoIngressoRepository.save(tipo);
            }
        }
    }

    @Transactional(readOnly = true)
    public List<Compra> buscarTodasCompras() {
        return compraRepository.findAllFetchIngressos();
    }

    @Transactional(readOnly = true)
    public Optional<Compra> buscarCompraPorId(Long id) {
        return compraRepository.findByIdFetchIngressos(id);
    }

    @Transactional(readOnly = true)
    public List<Compra> buscarComprasPorUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuário não encontrado com ID: " + usuarioId);
        }
        return compraRepository.findByUsuarioIdUsuarioFetchIngressos(usuarioId);
    }

    @Transactional
    public Compra atualizarStatus(Long id, StatusUpdateRequest request) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compra não encontrada"));

        compra.setStatus(request.getStatus());
        compraRepository.save(compra);
        return compraRepository.findByIdFetchIngressos(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compra não encontrada"));
    }

    @Transactional
    public void deletarCompra(Long id) {
        if (!compraRepository.existsById(id)) {
            throw new ResourceNotFoundException("Compra não encontrada com ID: " + id);
        }

        List<Ingresso> ingressosDaCompra = ingressoRepository.findByCompraIdCompra(id);
        restaurarQuantidadeTipoIngressoPorIngressos(ingressosDaCompra);

        for (Ingresso ingresso : ingressosDaCompra) {
            ingresso.setVendido(false);
            ingresso.setCompra(null);
            ingresso.setIngressoDisponivel(true);
        }
        ingressoRepository.saveAll(ingressosDaCompra);

        compraRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public long contarIngressosVendidosPorSessao(Long sessaoEventoId) {
        return ingressoRepository.countBySessaoEventoIdSessaoAndVendidoTrue(sessaoEventoId);
    }
}
