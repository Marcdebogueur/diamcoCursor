package com.diamco.v1.entities;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("CLIENT")
public class Client extends Utilisateur {}
