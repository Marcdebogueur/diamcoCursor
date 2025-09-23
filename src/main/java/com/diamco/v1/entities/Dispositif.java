package com.diamco.v1.entities;

import com.diamco.v1.entities.base.BaseEntity;
import com.diamco.v1.entities.enums.StatutDispositif;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Table(indexes = {
        @Index(name = "idx_dispositif_numero", columnList = "numeroSerie", unique = true),
        @Index(name = "idx_dispositif_zone", columnList = "zone")
})
public class Dispositif extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String numeroSerie;

    private String zone;
    private LocalDate dateInstallation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutDispositif statut = StatutDispositif.ACTIF;

    @ManyToOne
    private Client proprietaire;

    @ManyToOne
    private Technicien technicienReferent;

    private String typeFiltre;

    /** Relevés capteurs */
    @JsonIgnore
    @OneToMany(mappedBy = "dispositif", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("horodatage DESC")
    private List<ReleveCapteur> releves = new ArrayList<>();

    /** Interventions */
    @JsonIgnore
    @OneToMany(mappedBy = "dispositif", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("dateIntervention DESC")
    private List<Intervention> interventions = new ArrayList<>();


}
