package com.diamco.v1.entities;

import com.diamco.v1.entities.base.BaseEntity;
import com.diamco.v1.entities.enums.StatutAlerte;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(columnDefinition = "TEXT")
    private String photoPaths;

    private Instant dateSoumission;

    private Instant dateResolution;

    @Enumerated(EnumType.STRING)
    private StatutAlerte statut = StatutAlerte.NOUVELLE;

    @ManyToOne(fetch = FetchType.LAZY)
    private Client client;
}

