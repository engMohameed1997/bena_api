package com.bena.api.module.ebook.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EbookNoteDto {

    private UUID id;
    private Integer pageNumber;
    private String noteText;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
