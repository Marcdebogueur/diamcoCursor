package com.diamco.v1.entities.dtos;

import lombok.Data;

@Data
public class RegisterRequest {
    private String nom;
    private String prenom;
    private String telephone;
    private String adresse;
    private String email;
    private String password;
    private String role; // "CLIENT", "ADMIN", "TECHNICIEN", "SUPERADMIN"
    private String specialite; // optionnel si role = TECHNICIEN
}
