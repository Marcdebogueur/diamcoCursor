package com.diamco.v1.repository;

import com.diamco.v1.entities.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, String> {
    Optional<PasswordResetToken> findByEmailAndTokenAndUsedFalse(String email, String token);
}
