package org.example.repository;

import org.example.model.MediaSemanal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

public interface MediaSemanalRepository extends JpaRepository<MediaSemanal, Long> {

    Optional<MediaSemanal> findByMoedaAndInicioSemana(String moeda, LocalDate inicioSemana);

    List<MediaSemanal> findByMoedaOrderByInicioSemanaDesc(String moeda);
}
