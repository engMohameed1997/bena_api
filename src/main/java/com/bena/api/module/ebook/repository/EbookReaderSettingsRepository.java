package com.bena.api.module.ebook.repository;

import com.bena.api.module.ebook.entity.EbookReaderSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EbookReaderSettingsRepository extends JpaRepository<EbookReaderSettings, UUID> {

    Optional<EbookReaderSettings> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);
}
