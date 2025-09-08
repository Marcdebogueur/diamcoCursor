// src/main/java/com/diamco/v1/entities/base/BaseEntity.java
package com.diamco.v1.entities.base;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@MappedSuperclass
@Getter @Setter
public abstract class BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    /** Archivage logique et activité */
    @Column(nullable = false)
    private boolean archived = false;

    @Column(nullable = false)
    private boolean active = true;
}
