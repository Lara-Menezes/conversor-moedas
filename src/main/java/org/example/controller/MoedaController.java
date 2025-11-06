package org.example.controller;

import org.example.dto.MoedaDTO;
import org.example.service.MoedaService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class MoedaController {

    private final MoedaService servico;

    public MoedaController(MoedaService servico) {
        this.servico = servico;
    }

    @GetMapping("/currencies")
    public Map<String, String> listarMoedas() {
        return servico.listarMoedas();
    }

    @GetMapping("/convert")
    public MoedaDTO converter(
            @RequestParam(name = "from")  String from,
            @RequestParam(name = "to") String to,
            @RequestParam(name = "amount") double amount) {
        return servico.converter(from, to, amount);
    }
}
