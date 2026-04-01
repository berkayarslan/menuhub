package com.menuhub.api.submission;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateSubmissionRequest(
                @NotNull Long restaurantId,
                @NotBlank String sourceType,
                @NotBlank String rawText
) {
}