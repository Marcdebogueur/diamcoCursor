package com.diamco.v1.repository;

import com.diamco.v1.entities.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, String> {

    Optional<EmailVerificationToken> findByEmailAndTokenAndUsedFalse(String email, String token);

    // Supprimer les anciens tokens pour un email (optionnel, pour le nettoyage)
    void deleteByEmail(String email);
}