package com.bena.api.module.portfolio.repository;

import com.bena.api.module.portfolio.entity.PortfolioItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortfolioItemRepository extends JpaRepository<PortfolioItem, Long> {
    Page<PortfolioItem> findByWorkerIdAndIsActiveOrderByCreatedAtDesc(Long workerId, Boolean isActive, Pageable pageable);
    Page<PortfolioItem> findByIsFeaturedAndIsActiveOrderByCreatedAtDesc(Boolean isFeatured, Boolean isActive, Pageable pageable);
    Page<PortfolioItem> findByCategoryAndIsActiveOrderByCreatedAtDesc(String category, Boolean isActive, Pageable pageable);
}
