package com.bena.api.module.project.repository;

import com.bena.api.module.project.entity.Bid;
import com.bena.api.module.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface BidRepository extends JpaRepository<Bid, UUID> {

    Page<Bid> findByClient(User client, Pageable pageable);

    Page<Bid> findByProvider(User provider, Pageable pageable);

    Page<Bid> findByStatus(Bid.BidStatus status, Pageable pageable);

    Page<Bid> findByClientAndStatus(User client, Bid.BidStatus status, Pageable pageable);

    Page<Bid> findByProviderAndStatus(User provider, Bid.BidStatus status, Pageable pageable);

    List<Bid> findByClientAndStatusIn(User client, List<Bid.BidStatus> statuses);

    List<Bid> findByProviderAndStatusIn(User provider, List<Bid.BidStatus> statuses);

    @Query("SELECT b FROM Bid b WHERE b.status = :status AND b.expiresAt < :now")
    List<Bid> findExpiredBids(@Param("status") Bid.BidStatus status, @Param("now") LocalDateTime now);

    Long countByClient(User client);

    Long countByProvider(User provider);

    Long countByStatus(Bid.BidStatus status);
}
