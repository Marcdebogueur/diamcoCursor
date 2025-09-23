// src/main/java/com/diamco/v1/entities/Alerte.java
package com.diamco.v1.entities;

import com.diamco.v1.entities.base.BaseEntity;
import com.diamco.v1.entities.enums.StatutAlerte;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "alertes")
public class Alerte extends BaseEntity {

    @Column(nullable = false)
    private String titre;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    private String photoPath; // chemin du fichier stocké

    private Instant dateSoumission;

    private Instant dateResolution;

    @Enumerated(EnumType.STRING)
    private StatutAlerte statut = StatutAlerte.NOUVELLE;

    /** Tracabilité : un client peut avoir plusieurs alertes */
    @ManyToOne(fetch = FetchType.LAZY)
    private Client client;
}
