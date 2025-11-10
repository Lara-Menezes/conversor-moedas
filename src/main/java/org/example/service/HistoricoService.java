package org.example.service;

import org.example.model.TaxaConversao;
import org.example.repository.TaxaConversaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;

@Service
public class HistoricoService {

    private static final String API_URL = "https://api.frankfurter.app";
    private final TaxaConversaoRepository taxaConversaoRepository;

    public HistoricoService(TaxaConversaoRepository taxaConversaoRepository) {
        this.taxaConversaoRepository = taxaConversaoRepository;
    }

    /**
     * Importa e salva taxas de um intervalo de tempo
     */
    public void importarTaxasSemanais(String from, String to, LocalDate inicio, LocalDate fim) {

        // Verificações da datas para ser revisadas ou movidas
        LocalDate hoje = LocalDate.now(ZoneId.of("UTC"));

        if (!fim.isBefore(hoje)) fim = hoje.minusDays(1);
        if (inicio.isAfter(fim)) {
            LocalDate tmp = inicio;
            inicio = fim;
            fim = tmp;
        }
        if (!inicio.isBefore(fim)) {
            System.out.println("Intervalo inválido (" + inicio + " .. " + fim + ")");
            return;
        }

        // Requisição para taxas diárias da semana passada
        String url = String.format("%s/%s..%s?from=%s&to=%s", API_URL, inicio, fim, from, to);
        System.out.println("Buscando dados da semana passada: " + url);

        RestTemplate restTemplate = new RestTemplate();

        //Tratamento de erros
        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response == null || !response.containsKey("rates")) {
                System.out.println("Resposta vazia da API");
                return;
            }

            Map<String, Map<String, Double>> rates = (Map<String, Map<String, Double>>) response.get("rates");
            if (rates == null || rates.isEmpty()) {
                System.out.println("Nenhuma taxa encontrada no período " + inicio + " até " + fim);
                return;
            }

            //Percorre todas as datas do período informado
            int countNovas = 0;
            for (Map.Entry<String, Map<String, Double>> entry : rates.entrySet()) {
                LocalDate data = LocalDate.parse(entry.getKey());
                Double valor = entry.getValue().get(to);
                if (valor == null) continue;

                if (taxaConversaoRepository.existsByMoedaAndData(to, data)) continue;

                // Salva os dados de taxa no banco
                TaxaConversao taxa = new TaxaConversao();
                taxa.setMoedaBase(from);
                taxa.setMoeda(to);
                taxa.setData(data);
                taxa.setTaxa(BigDecimal.valueOf(valor));
                taxaConversaoRepository.save(taxa);
                countNovas++;
            }

            System.out.println(countNovas + " novas taxas salvas de " + from + " para " + to + " entre " + inicio + " e " + fim);

        } catch (Exception e) {
            System.err.println("Erro ao acessar API: " + e.getMessage());
        }
    }
}
