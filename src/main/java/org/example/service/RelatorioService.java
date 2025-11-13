package org.example.service;

import org.example.model.MediaSemanal;
import org.example.repository.TaxaConversaoRepository;
import org.example.repository.MediaSemanalRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Service
public class RelatorioService {

    private final TaxaConversaoRepository taxaCambioRepository;
    private final MediaSemanalRepository relatorioSemanalRepository;

    public RelatorioService(TaxaConversaoRepository taxaCambioRepository,
                            MediaSemanalRepository relatorioSemanalRepository) {
        this.taxaCambioRepository = taxaCambioRepository;
        this.relatorioSemanalRepository = relatorioSemanalRepository;
    }

    /**
     * Lê relatório (cacheado). Chave: moeda + inicioSemana
     */

    @Cacheable(value = "relatoriosSemanais", key = "T(org.example.util.CacheKeys).relatorioKeyBySemana(#moeda, #inicioSemana)")
    public MediaSemanal getRelatorioCached(String moeda, LocalDate inicioSemana) {
        return relatorioSemanalRepository.findByMoedaAndInicioSemana(moeda, inicioSemana).orElse(null);
    }

    /**
     * Gera relatório baseado na variação da taxa atual e média das taxas da semana passada
     *
     */

    @Transactional
    @CacheEvict(
            value = "relatoriosSemanais",
            key = "T(org.example.utils.CacheKeys).relatorioKeyBySemana(#moeda, #dataAtual.minusWeeks(1).with(T(java.time.DayOfWeek).MONDAY))")
    public MediaSemanal gerarRelatorioSemanal(String moeda, BigDecimal taxaAtual, LocalDate dataAtual) {


        if (taxaAtual == null) {
            System.out.println("Nenhuma taxa informada para " + moeda);
            return null;
        }

        LocalDate inicioSemanaAnterior = dataAtual.minusWeeks(1).with(java.time.DayOfWeek.MONDAY);
        LocalDate fimSemanaAnterior = inicioSemanaAnterior.plusDays(6);

        // já existe?
        var existente = relatorioSemanalRepository.findByMoedaAndInicioSemana(moeda, inicioSemanaAnterior);
        if (existente.isPresent()) {
            System.out.println("Relatório já existe para " + moeda + " (" + inicioSemanaAnterior + ")");
            return existente.get();
        }

        BigDecimal mediaSemanaAnterior = taxaCambioRepository.calcularMediaPorPeriodo(moeda, inicioSemanaAnterior, fimSemanaAnterior);
        BigDecimal crescimento = BigDecimal.ZERO;
        if (mediaSemanaAnterior != null && mediaSemanaAnterior.compareTo(BigDecimal.ZERO) != 0) {
            crescimento = taxaAtual.subtract(mediaSemanaAnterior)
                    .divide(mediaSemanaAnterior, 8, RoundingMode.HALF_UP)
                    .setScale(8, RoundingMode.HALF_UP);
        }

        MediaSemanal relatorio = new MediaSemanal();
        relatorio.setMoeda(moeda);
        relatorio.setInicioSemana(inicioSemanaAnterior);
        relatorio.setFimSemana(fimSemanaAnterior);
        relatorio.setMediaSemana(mediaSemanaAnterior);
        relatorio.setValorAtual(taxaAtual);
        relatorio.setTaxaCrescimento(crescimento);
        relatorio.setCriadoEm(OffsetDateTime.now(ZoneOffset.UTC));

        return relatorioSemanalRepository.save(relatorio);
    }
}