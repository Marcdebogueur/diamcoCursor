package com.diamco.v1.entities;

import com.diamco.v1.entities.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ReleveCapteur extends BaseEntity {

    @ManyToOne(optional = false)
    private Dispositif dispositif;

    @Column(nullable = false)
    private Instant horodatage;

    private Double ph;           // pH
    private Double turbidite;    // NTU
    private Double tds;          // ppm
    private Double volumeFiltre; // litres cumulés
}
