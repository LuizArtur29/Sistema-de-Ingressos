package com.vendaingressos.service;

import com.vendaingressos.model.TipoIngresso;
import com.vendaingressos.model.Ingresso;
import com.vendaingressos.repository.IngressoRepository;
import com.vendaingressos.repository.TipoIngressoRepository; // Necessário criar a interface JpaRepository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TipoIngressoService {

    @Autowired
    private TipoIngressoRepository tipoIngressoRepository;

    @Autowired
    private IngressoRepository ingressoRepository;

    @Transactional
    public TipoIngresso salvar(TipoIngresso tipo) {
        TipoIngresso tipoSalvo = tipoIngressoRepository.save(tipo);
        gerarIngressosFaltantes(tipoSalvo);
        return tipoSalvo;
    }

    @Transactional
    public List<TipoIngresso> listarPorSessao(Long sessaoId) {
        List<TipoIngresso> tipos = tipoIngressoRepository.findBySessaoIdSessao(sessaoId);
        tipos.forEach(this::gerarIngressosFaltantes);
        return tipos;
    }

    private void gerarIngressosFaltantes(TipoIngresso tipo) {
        if (tipo.getIdTipoIngresso() == null || tipo.getSessao() == null || tipo.getQuantidadeTotal() == null) {
            return;
        }

        long ingressosJaGerados = ingressoRepository.countByTipoIngressoIdTipoIngresso(tipo.getIdTipoIngresso());
        int faltantes = tipo.getQuantidadeTotal() - (int) ingressosJaGerados;

        if (faltantes <= 0) {
            return;
        }

        List<Ingresso> novosIngressos = new ArrayList<>(faltantes);
        for (int i = 0; i < faltantes; i++) {
            Ingresso ingresso = new Ingresso();
            ingresso.setSessaoEvento(tipo.getSessao());
            ingresso.setTipoIngresso(tipo);
            ingresso.setPreco(tipo.getPreco());
            ingresso.setIngressoDisponivel(true);
            ingresso.setVendido(false);
            ingresso.setCompra(null);
            novosIngressos.add(ingresso);
        }

        ingressoRepository.saveAll(novosIngressos);
    }
}
