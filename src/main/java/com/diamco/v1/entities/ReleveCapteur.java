package com.diamco.v1.entities;

import com.diamco.v1.entities.base.BaseEntity;
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
public class ReleveCapteur extends BaseEntity {

    @ManyToOne(optional = false)
    private Dispositif dispositif;

    @Column(nullable = false)
    private Instant horodatage;

    private Double ph;
    private Double turbidite;
    private Double tds;
    private Double volumeFiltre;
}

