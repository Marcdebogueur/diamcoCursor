package com.diamco.v1.entities;

import com.diamco.v1.entities.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Intervention extends BaseEntity {
    @ManyToOne
    private Dispositif dispositif;

    private LocalDate dateIntervention;

    private String description;
}

