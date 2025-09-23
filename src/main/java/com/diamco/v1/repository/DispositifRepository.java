// src/main/java/com/diamco/v1/repository/DispositifRepository.java
package com.diamco.v1.repository;

import com.diamco.v1.entities.Dispositif;
import com.diamco.v1.entities.enums.StatutDispositif;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DispositifRepository extends JpaRepository<Dispositif, String> {

    Optional<Dispositif> findByNumeroSerie(String numeroSerie);
    List<Dispositif> findByStatut(StatutDispositif statut);
    List<Dispositif> findByNumeroSerieContainingIgnoreCaseOrZoneContainingIgnoreCase(String numero, String zone);
}
