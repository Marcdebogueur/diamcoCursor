// src/main/java/com/diamco/v1/repository/AlerteRepository.java
package com.diamco.v1.repository;

import com.diamco.v1.entities.Alerte;
import com.diamco.v1.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlerteRepository extends JpaRepository<Alerte, String> {
    List<Alerte> findByClient(Client client);
}
