package com.diamco.v1.entities;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role", length = 20)
@Table(name = "users")
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "role"   // ⚡ important : correspond au champ JSON
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Client.class, name = "CLIENT"),
        @JsonSubTypes.Type(value = Admin.class, name = "ADMIN"),
        @JsonSubTypes.Type(value = Technicien.class, name = "TECHNICIEN"),
        @JsonSubTypes.Type(value = SuperAdmin.class, name = "SUPERADMIN")
})
public abstract class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String nom;
    private String prenom;
    private String telephone;
    private String adresse;

    @Column(unique = true, nullable = false)
    @Email
    private String email;

    private String mdpHash;

    private boolean emailVerifie = false;
    private boolean isAuthentificated = false;

    @Column(name = "role", insertable = false, updatable = false)
    private String role;
}
