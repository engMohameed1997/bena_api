package com.bena.api.module.ebook.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EbookNoteRequest {

    @NotNull(message = "رقم الصفحة مطلوب")
    @Min(value = 1, message = "رقم الصفحة يجب أن يكون 1 أو أكثر")
    private Integer pageNumber;

    @NotBlank(message = "نص الملاحظة مطلوب")
    private String noteText;
}
