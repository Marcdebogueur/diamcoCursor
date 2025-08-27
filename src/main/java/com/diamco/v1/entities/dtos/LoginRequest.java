
package com.diamco.v1.entities.dtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class LoginRequest {
    @Column(unique = true, nullable = false)
    @Email
    private String email;
    private String motDePasse;  // pas "mdpHash" ici, car c’est le mot de passe brut
}

