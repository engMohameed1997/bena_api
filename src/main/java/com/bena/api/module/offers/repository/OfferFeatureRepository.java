package com.bena.api.module.offers.repository;

import com.bena.api.module.offers.entity.OfferFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OfferFeatureRepository extends JpaRepository<OfferFeature, Long> {

    List<OfferFeature> findByOfferIdOrderByDisplayOrderAsc(UUID offerId);

    void deleteByOfferId(UUID offerId);
}
