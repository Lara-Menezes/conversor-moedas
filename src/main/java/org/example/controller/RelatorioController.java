package org.example.controller;

import org.example.model.MediaSemanal;
import org.example.repository.MediaSemanalRepository;
import org.example.repository.TaxaConversaoRepository;
import org.example.service.HistoricoService;
import org.example.service.RelatorioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/relatorios")
@CrossOrigin(origins = "*") // permite acesso do frontend
public class RelatorioController {

    private final MediaSemanalRepository mediaSemanalRepository;
    private final RelatorioService relatorioService;
    private final HistoricoService historicoService;
    private final TaxaConversaoRepository taxaConversaoRepository;

    public RelatorioController(MediaSemanalRepository mediaSemanalRepository,
                               RelatorioService relatorioService,
                               HistoricoService historicoService,
                               TaxaConversaoRepository taxaConversaoRepository) {
        this.mediaSemanalRepository = mediaSemanalRepository;
        this.relatorioService = relatorioService;
        this.historicoService = historicoService;
        this.taxaConversaoRepository = taxaConversaoRepository;
    }

    // Retorna todos os relatórios
    @GetMapping
    public List<MediaSemanal> listarTodos() {
        return mediaSemanalRepository.findAll();
    }

    // Filtra relatórios por moeda
    @GetMapping("/{moeda}")
    public List<MediaSemanal> listarPorMoeda(@PathVariable String moeda) {
        return mediaSemanalRepository.findByMoedaOrderByInicioSemanaDesc(moeda);
    }

    // Gera relatórios semanais (anterior + atual)
    @PostMapping("/gerar/por-par")
    public ResponseEntity<String> gerarRelatorioPorPar(
            @RequestParam String from,
            @RequestParam String to) {

        if (from.equalsIgnoreCase(to)) {
            return ResponseEntity.badRequest()
                    .body("Os códigos de moeda 'from' e 'to' não podem ser iguais");
        }

        try {
            // DATAS- revisar ou mover
            LocalDate hoje = LocalDate.now(ZoneId.of("UTC"));
            LocalDate inicioSemanaAnterior = hoje.minusWeeks(1).with(DayOfWeek.MONDAY);
            LocalDate fimSemanaAnterior = inicioSemanaAnterior.plusDays(6);

            // Importa histórico da semana anterior
            historicoService.importarTaxasSemanais(from, to, inicioSemanaAnterior, fimSemanaAnterior);

            // Busca taxa atual via API
            RestTemplate restTemplate = new RestTemplate();
            String url = String.format("https://api.frankfurter.app/latest?from=%s&to=%s", from, to);
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            Map<String, Double> rates = (Map<String, Double>) response.get("rates");
            Double valorAtual = rates.get(to);

            if (valorAtual == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Não foi possível obter a taxa atual");
            }

            // Gera relatório usando RelatorioService
            relatorioService.gerarRelatorioSemanal(to, BigDecimal.valueOf(valorAtual), hoje);

            return ResponseEntity.ok("Relatório gerado com a taxa atual: " + valorAtual);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao gerar relatório: " + e.getMessage());
        }
    }
}
