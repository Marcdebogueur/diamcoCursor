package com.diamco.v1.repository;

import com.diamco.v1.entities.Alerte;
import com.diamco.v1.entities.enums.StatutAlerte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlerteRepository extends JpaRepository<Alerte, String> {
    List<Alerte> findByClient_Id(String clientId);
    List<Alerte> findByStatut(StatutAlerte statut);
}

