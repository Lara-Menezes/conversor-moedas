package org.example.controller;

import org.example.dto.MoedaDTO;
import org.example.service.MoedaService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/convert")
@CrossOrigin(origins = "*")
public class MoedaController {

    private final MoedaService servico;

    public MoedaController(MoedaService servico) {
        this.servico = servico;
    }

    @GetMapping
    public MoedaDTO converter(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam double amount) {
        return servico.converter(from, to, amount);
    }
}
