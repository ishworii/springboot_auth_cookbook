package com.ishwor.authcookbook.common.journal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record JournalCreateRequest(
        @NotBlank @Size(max = 255) String title,
        @NotBlank String content
) {}
