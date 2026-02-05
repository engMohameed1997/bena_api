package com.bena.api.module.ebook.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReaderSettingsDto {

    private Integer fontSize;
    private Boolean isDarkMode;
}
