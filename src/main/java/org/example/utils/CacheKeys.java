package org.example.utils;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CacheKeys {

    private CacheKeys() {}

    public static String relatorioKey(String moeda, LocalDate dataAtual, BigDecimal taxaAtual) {
        LocalDate data = dataAtual != null ? dataAtual : LocalDate.now();
        String taxa = taxaAtual != null ? taxaAtual.toPlainString() : "0";
        // usamos inicio da semana para chave (consistente com sua lógica)
        LocalDate inicioSemana = data.minusWeeks(1).with(java.time.DayOfWeek.MONDAY);
        return moeda + "-" + inicioSemana.toString() + "-" + taxa;
    }

    // Se quiser só por moeda+semana:
    public static String relatorioKeyBySemana(String moeda, LocalDate inicioSemana) {
        LocalDate inicio = inicioSemana != null ? inicioSemana : LocalDate.now().with(java.time.DayOfWeek.MONDAY);
        return moeda + "-" + inicio.toString();
    }
}
