package com.diamco.v1.entities;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends Utilisateur {}
