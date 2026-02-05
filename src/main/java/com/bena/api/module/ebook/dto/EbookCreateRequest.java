package com.bena.api.module.ebook.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EbookCreateRequest {

    @NotBlank(message = "عنوان الكتاب مطلوب")
    @Size(max = 200, message = "العنوان يجب أن لا يتجاوز 200 حرف")
    private String title;

    private String description;

    @NotBlank(message = "التصنيف مطلوب")
    private String category;

    @NotNull(message = "السعر مطلوب")
    @DecimalMin(value = "0.0", message = "السعر يجب أن يكون 0 أو أكثر")
    private BigDecimal price;

    @Builder.Default
    private String currency = "IQD";
}
