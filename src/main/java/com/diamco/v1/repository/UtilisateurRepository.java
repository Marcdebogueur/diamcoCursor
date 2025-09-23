package com.diamco.v1.repository;

import com.diamco.v1.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, String> {
    Utilisateur findByEmail(String email);
    boolean existsByEmail(String email);

    Utilisateur findByNom(String username);
    Utilisateur findByTelephone(String telephone);

}
