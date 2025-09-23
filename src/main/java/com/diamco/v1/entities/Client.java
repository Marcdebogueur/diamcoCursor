package com.diamco.v1.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("CLIENT")
public class Client extends Utilisateur {
    /** Dispositifs possédés par le client */
    @JsonIgnore
    @OneToMany(mappedBy = "proprietaire")
    private List<Dispositif> dispositifs = new ArrayList<>();
}
