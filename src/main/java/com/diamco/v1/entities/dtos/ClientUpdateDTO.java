package com.diamco.v1.entities.dtos;

import lombok.Data;

@Data
public class ClientUpdateDTO {
    private String nom;
    private String prenom;
    private String adresse;
}
