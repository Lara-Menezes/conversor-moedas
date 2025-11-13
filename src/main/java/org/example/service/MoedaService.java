package org.example.service;

import org.example.dto.MoedaDTO;
import org.example.model.TaxaConversao;
import org.example.repository.TaxaConversaoRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class MoedaService {

    private static final String API_URL = "https://api.frankfurter.app/latest";

    // coloquei para rest ser criado uma unica vez
    private final RestTemplate  restTemplate;
    private final TaxaConversaoRepository taxaConversaoRepository;
    private final RelatorioService relatorioService;

    public MoedaService(TaxaConversaoRepository taxaConversaoRepository,
                        RelatorioService relatorioService) {
        this.restTemplate = new RestTemplate();
        this.taxaConversaoRepository = taxaConversaoRepository;
        this.relatorioService = relatorioService;
    }

    // Lista todas as moedas
   // @Cacheable(value = "moedasDisponiveis")
    public java.util.Map<String,String> listarMoedas() {
        String url = "https://api.frankfurter.app/currencies";
        return restTemplate.getForObject(url, java.util.Map.class);
    }

    public MoedaDTO converter(String from, String to, double amount) {

        var moedas = listarMoedas();
        if (!moedas.containsKey(from) || !moedas.containsKey(to)) {
            throw new IllegalArgumentException("Par de moedas inválido: " + from + " -> " + to);
        }
        if (from.equals(to)) {
            throw new IllegalArgumentException("Moedas iguais não podem ser convertidas.");
        }

        String url = String.format("%s?amount=%f&from=%s&to=%s", API_URL, amount, from, to);

        java.util.Map<String, Object> response = restTemplate.getForObject(url, java.util.Map.class);
        java.util.Map<String, Number> rates = (java.util.Map<String, Number>) response.get("rates");

        //conversao geral para troca double e int para não aver problema e erro.
        Number rateNumber = rates.get(to);

        double convertedAmount = rateNumber.doubleValue();
        double rate = convertedAmount / amount;

        MoedaDTO dto = new MoedaDTO();
        dto.setFrom(from);
        dto.setTo(to);
        dto.setAmount(amount);
        dto.setRate(rate);
        dto.setConvertedAmount(convertedAmount);

        System.out.println("Resposta da API Frankfurter: " + response);

        // Salva taxa diária no banco
        LocalDate hoje = LocalDate.now();
        boolean existe = taxaConversaoRepository.existsByMoedaAndData(to, hoje);
        if (!existe) {
            TaxaConversao taxa = new TaxaConversao();
            taxa.setMoedaBase(from);
            taxa.setMoeda(to);
            taxa.setData(hoje);
            taxa.setTaxa(BigDecimal.valueOf(rate));
            taxaConversaoRepository.save(taxa);

            // Atualiza relatório semanal automaticamente usando taxa atual
            relatorioService.gerarRelatorioSemanal(to, BigDecimal.valueOf(rate), hoje);
        }
        return dto;
    }
}
