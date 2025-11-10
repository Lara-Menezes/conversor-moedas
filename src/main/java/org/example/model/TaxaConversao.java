package org.example.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidade que representa uma taxa de conversão diária da API
 */
@Entity
@Table(name = "taxa_cambio", indexes = {
        @Index(name = "idx_moeda_data", columnList = "moeda, data_taxa")
})
public class TaxaConversao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 8, nullable = false)
    private String moeda;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal taxa;

    @Column(name = "data_taxa", nullable = false)
    private LocalDate data;

    @Column(length = 8)
    private String moedaBase;

    public TaxaConversao() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMoeda() { return moeda; }
    public void setMoeda(String moeda) { this.moeda = moeda; }

    public BigDecimal getTaxa() { return taxa; }
    public void setTaxa(BigDecimal taxa) { this.taxa = taxa; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public String getMoedaBase() { return moedaBase; }
    public void setMoedaBase(String moedaBase) { this.moedaBase = moedaBase; }
}
