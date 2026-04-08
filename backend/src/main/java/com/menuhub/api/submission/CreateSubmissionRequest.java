package com.menuhub.api.submission;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateSubmissionRequest(
                @NotNull Long restaurantId,
                @NotBlank String sourceType,
                String rawText,
                @Valid List<SubmissionMenuRowRequest> items
) {}