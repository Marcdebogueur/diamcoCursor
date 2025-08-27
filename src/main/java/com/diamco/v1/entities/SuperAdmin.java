package com.diamco.v1.entities;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("SUPERADMIN")
public class SuperAdmin extends Utilisateur {}