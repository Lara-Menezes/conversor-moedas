package org.example.repository;

import org.example.model.TaxaConversao;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TaxaConversaoRepository extends JpaRepository<TaxaConversao, Long> {

    // Verifica se já existe taxa registrada em uma data
    boolean existsByMoedaAndData(String moeda, LocalDate data);

    // Calcula a média da taxa em um intervalo
    @Query("SELECT CAST(AVG(CAST(t.taxa AS double)) AS bigdecimal) FROM TaxaConversao t WHERE t.moeda = :moeda AND t.data BETWEEN :inicio AND :fim")
    BigDecimal calcularMediaPorPeriodo(@Param("moeda") String moeda,
                                       @Param("inicio") LocalDate inicio,
                                       @Param("fim") LocalDate fim);

    // Busca a taxa mais recente
    @Query("SELECT t.taxa FROM TaxaConversao t WHERE t.moeda = :moeda ORDER BY t.data DESC")
    List<BigDecimal> buscarTaxasMaisRecentes(@Param("moeda") String moeda, org.springframework.data.domain.Pageable pageable);

    default BigDecimal buscarTaxaMaisRecente(String moeda) {
        List<BigDecimal> taxas = buscarTaxasMaisRecentes(moeda, PageRequest.of(0, 1));
        return taxas.isEmpty() ? null : taxas.get(0);
    }

}
