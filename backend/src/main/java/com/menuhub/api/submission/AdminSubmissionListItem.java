package com.menuhub.api.submission;

import java.time.LocalDateTime;

public record AdminSubmissionListItem(
                Long id,
                Long restaurantId,
                String restaurantName,
                String sourceType,
                String rawText,
                String status,
                LocalDateTime createdAt
) {}