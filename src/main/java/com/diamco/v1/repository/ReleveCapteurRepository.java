package com.diamco.v1.repository;

import com.diamco.v1.entities.ReleveCapteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface ReleveCapteurRepository extends JpaRepository<ReleveCapteur, String> {

    @Query("select r from ReleveCapteur r where r.dispositif.id = :dispositifId and (:start is null or r.horodatage >= :start) and (:end is null or r.horodatage <= :end) order by r.horodatage desc")
    List<ReleveCapteur> findByDispositifBetween(@Param("dispositifId") String dispositifId,
                                                @Param("start") Instant start,
                                                @Param("end") Instant end);
}

