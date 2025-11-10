package org.example;

import org.example.service.HistoricoService;
import org.example.service.RelatorioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@SpringBootApplication
public class ConversorApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConversorApplication.class, args);
    }
}
