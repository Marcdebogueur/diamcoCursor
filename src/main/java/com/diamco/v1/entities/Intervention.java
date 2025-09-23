package com.diamco.v1.entities;

import com.diamco.v1.entities.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Intervention extends BaseEntity {

    @ManyToOne(optional = false)
    private Dispositif dispositif;

    @ManyToOne(optional = false)
    private Technicien technicien;

    @Column(nullable = false)
    private Instant dateIntervention;

    @Lob
    private String description;

    @Lob
    private String resultat;
}
