package com.diamco.v1.entities.dtos;

import lombok.Data;

@Data
public class TechnicienProfilDTO {
    private String nom;
    private String prenom;
    private String telephone;
    private String adresse;
    private String email;
    private String specialite;
}
