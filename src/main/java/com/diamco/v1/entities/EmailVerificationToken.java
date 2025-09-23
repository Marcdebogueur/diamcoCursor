package com.diamco.v1.entities;

import com.diamco.v1.entities.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "email_verification_tokens")
public class EmailVerificationToken extends BaseEntity {

    private String email;
    private String token;
    private LocalDateTime expiration;
    private boolean used = false;
}