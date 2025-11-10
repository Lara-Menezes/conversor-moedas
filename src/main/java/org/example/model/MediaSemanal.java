package org.example.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * Entidade que armazena o relatório semanal com média e taxa de crescimento
 */
@Entity
@Table(name = "relatorio_semanal", uniqueConstraints = {
        @UniqueConstraint(name = "uk_semana_moeda", columnNames = {"inicio_semana", "moeda"})
})
public class MediaSemanal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 8, nullable = false)
    private String moeda;

    @Column(name = "inicio_semana", nullable = false)
    private LocalDate inicioSemana;

    @Column(name = "fim_semana", nullable = false)
    private LocalDate fimSemana;

    @Column(name = "media_semana", precision = 19, scale = 8, nullable = false)
    private BigDecimal mediaSemana;

    @Column(name = "valor_atual", precision = 19, scale = 8)
    private BigDecimal valorAtual;

    @Column(name = "taxa_crescimento", precision = 19, scale = 8)
    private BigDecimal taxaCrescimento;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm;

    public MediaSemanal() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMoeda() { return moeda; }
    public void setMoeda(String moeda) { this.moeda = moeda; }

    public LocalDate getInicioSemana() { return inicioSemana; }
    public void setInicioSemana(LocalDate inicioSemana) { this.inicioSemana = inicioSemana; }

    public LocalDate getFimSemana() { return fimSemana; }
    public void setFimSemana(LocalDate fimSemana) { this.fimSemana = fimSemana; }

    public BigDecimal getMediaSemana() { return mediaSemana; }
    public void setMediaSemana(BigDecimal mediaSemana) { this.mediaSemana = mediaSemana; }

    public BigDecimal getValorAtual() { return valorAtual; }
    public void setValorAtual(BigDecimal valorAtual) { this.valorAtual = valorAtual; }

    public BigDecimal getTaxaCrescimento() { return taxaCrescimento; }
    public void setTaxaCrescimento(BigDecimal taxaCrescimento) { this.taxaCrescimento = taxaCrescimento; }

    public OffsetDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(OffsetDateTime criadoEm) { this.criadoEm = criadoEm; }

}