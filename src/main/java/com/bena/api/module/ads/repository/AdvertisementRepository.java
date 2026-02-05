package com.bena.api.module.ads.repository;

import com.bena.api.module.ads.entity.Advertisement;
import com.bena.api.module.ads.enums.AdSection;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface AdvertisementRepository extends JpaRepository<Advertisement, UUID> {

    @EntityGraph(attributePaths = {"sections"})
    @Query("select distinct a from Advertisement a join a.sections s " +
            "where a.active = true " +
            "and s = :section " +
            "and (a.startAt is null or a.startAt <= :now) " +
            "and (a.endAt is null or a.endAt >= :now) " +
            "order by a.priority asc, a.createdAt desc")
    List<Advertisement> findActiveAdsBySection(@Param("section") AdSection section, @Param("now") OffsetDateTime now);
}
