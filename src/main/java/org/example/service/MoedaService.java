package org.example.service;

import org.example.dto.MoedaDTO;
import org.example.model.TaxaConversao;
import org.example.repository.TaxaConversaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class MoedaService {

    private static final String API_URL = "https://api.frankfurter.app/latest";

    private final TaxaConversaoRepository taxaConversaoRepository;
    private final RelatorioService relatorioService;

    public MoedaService(TaxaConversaoRepository taxaConversaoRepository,
                        RelatorioService relatorioService) {
        this.taxaConversaoRepository = taxaConversaoRepository;
        this.relatorioService = relatorioService;
    }

    // Lista todas as moedas
    public java.util.Map<String,String> listarMoedas() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.frankfurter.app/currencies";
        return restTemplate.getForObject(url, java.util.Map.class);
    }

    public MoedaDTO converter(String from, String to, double amount) {
        RestTemplate restTemplate = new RestTemplate();
        String url = String.format("%s?amount=%f&from=%s&to=%s", API_URL, amount, from, to);

        java.util.Map<String, Object> response = restTemplate.getForObject(url, java.util.Map.class);
        java.util.Map<String, Double> rates = (java.util.Map<String, Double>) response.get("rates");

        double convertedAmount = rates.get(to);
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
