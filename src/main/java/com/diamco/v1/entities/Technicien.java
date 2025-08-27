package com.diamco.v1.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("TECHNICIEN")
public class Technicien extends Utilisateur {
    private String specialite;
}
