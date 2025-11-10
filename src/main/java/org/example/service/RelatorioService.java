package org.example.service;

import org.example.model.MediaSemanal;
import org.example.repository.TaxaConversaoRepository;
import org.example.repository.MediaSemanalRepository;
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
     * Gera relatório baseado na variação da taxa atual e média das taxas da semana passada
     *
     */

    @Transactional
    public MediaSemanal gerarRelatorioSemanal(String moeda, BigDecimal taxaAtual, LocalDate dataAtual) {

        if (taxaAtual == null) {
            System.out.println("Nenhuma taxa informada para " + moeda);
            return null;
        }

        // DATAS - Semana anterior (segunda a domingo)
        LocalDate inicioSemanaAnterior = dataAtual.minusWeeks(1).with(java.time.DayOfWeek.MONDAY);
        LocalDate fimSemanaAnterior = inicioSemanaAnterior.plusDays(6);

        // Calcula média da semana passada
        BigDecimal mediaSemanaAnterior = taxaCambioRepository.calcularMediaPorPeriodo(moeda, inicioSemanaAnterior, fimSemanaAnterior);
        if (mediaSemanaAnterior == null) {
            System.out.println("Sem média da semana passada para " + moeda + " (" + inicioSemanaAnterior + ")");
        }

        // Calcula a variação da taxa - crescimento ou queda
        BigDecimal crescimento = BigDecimal.ZERO;
        if (mediaSemanaAnterior != null && mediaSemanaAnterior.compareTo(BigDecimal.ZERO) != 0) {
            crescimento = taxaAtual.subtract(mediaSemanaAnterior)
                    .divide(mediaSemanaAnterior, 8, RoundingMode.HALF_UP)
                    .setScale(8, RoundingMode.HALF_UP);
        }

        // Cria o objeto de relatório
        MediaSemanal relatorio = new MediaSemanal();
        relatorio.setMoeda(moeda);
        relatorio.setInicioSemana(inicioSemanaAnterior);
        relatorio.setFimSemana(fimSemanaAnterior);
        relatorio.setMediaSemana(mediaSemanaAnterior);
        relatorio.setValorAtual(taxaAtual);
        relatorio.setTaxaCrescimento(crescimento);
        relatorio.setCriadoEm(OffsetDateTime.now(ZoneOffset.UTC));

        try {
            return relatorioSemanalRepository.save(relatorio);
        } catch (Exception e) {
            System.out.println("Relatório já existe para " + moeda + " em " + dataAtual);
            return relatorioSemanalRepository.findByMoedaAndInicioSemana(moeda, dataAtual).orElse(null);
        }
    }
}