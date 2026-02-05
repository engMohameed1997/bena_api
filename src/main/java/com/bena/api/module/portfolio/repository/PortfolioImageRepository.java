package com.bena.api.module.portfolio.repository;

import com.bena.api.module.portfolio.entity.PortfolioImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortfolioImageRepository extends JpaRepository<PortfolioImage, Long> {
    List<PortfolioImage> findByPortfolioItemIdOrderByDisplayOrderAsc(Long portfolioItemId);
}
