package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

// tive que tirar o banco da execução por enquanto. já que não estava em uso por agora
//se for tirar a parte do exclude, lembrar de colocar certo o banco para não da erro
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class ConversorApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConversorApplication.class, args);
    }
}
