package org.example.service;

import org.example.dto.MoedaDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;
import java.util.Map;

@Service
public class MoedaService {

    private static final String API_URL = "https://api.frankfurter.app/latest";

    //lista todas as moedas pra seleção de conversão
    public Map<String,String> listarMoedas(){
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.frankfurter.app/currencies";// acessa apenas as currencies da api (ex.: BRL,USD)
        return restTemplate.getForObject(url, Map.class);
    }


    public MoedaDTO converter(String from, String to, double amount) {
        RestTemplate restTemplate = new RestTemplate();
        String url = String.format("%s?amount=%f&from=%s&to=%s", API_URL, amount, from, to);

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        Map<String, Double> rates = (Map<String, Double>) response.get("rates");

        double convertedAmount = rates.get(to); // já vem convertido
        double rate = convertedAmount / amount;

        MoedaDTO dto = new MoedaDTO();
        dto.setFrom(from);
        dto.setTo(to);
        dto.setAmount(amount);
        dto.setRate(rate);
        dto.setConvertedAmount(convertedAmount);
        System.out.println("Resposta da API Frankfurter: " + response);

        return dto;
    }
}
