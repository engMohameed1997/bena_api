package com.bena.api.module.offers.repository;

import com.bena.api.module.offers.entity.OfferImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OfferImageRepository extends JpaRepository<OfferImage, Long> {

    List<OfferImage> findByOfferIdOrderByDisplayOrderAsc(UUID offerId);

    void deleteByOfferId(UUID offerId);
}
