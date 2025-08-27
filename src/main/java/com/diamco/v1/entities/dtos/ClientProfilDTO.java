package com.diamco.v1.entities.dtos;

import lombok.Data;

@Data
public class ClientProfilDTO {
    private String nom;
    private String prenom;
    private String telephone;
    private String adresse;
    private String email;
}
