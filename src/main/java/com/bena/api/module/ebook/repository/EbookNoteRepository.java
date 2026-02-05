package com.bena.api.module.ebook.repository;

import com.bena.api.module.ebook.entity.EbookNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EbookNoteRepository extends JpaRepository<EbookNote, UUID> {

    // ملاحظات المستخدم على كتاب
    List<EbookNote> findByUserIdAndEbookIdOrderByPageNumberAsc(UUID userId, UUID ebookId);

    // ملاحظات صفحة محددة
    List<EbookNote> findByUserIdAndEbookIdAndPageNumber(UUID userId, UUID ebookId, Integer pageNumber);

    // حذف ملاحظات المستخدم على كتاب
    void deleteByUserIdAndEbookId(UUID userId, UUID ebookId);

    // عدد ملاحظات المستخدم
    long countByUserIdAndEbookId(UUID userId, UUID ebookId);
}
