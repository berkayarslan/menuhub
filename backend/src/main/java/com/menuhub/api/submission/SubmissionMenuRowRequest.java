package com.menuhub.api.submission;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SubmissionMenuRowRequest(
                @NotBlank String category,
                @NotBlank String name,
                @NotNull @DecimalMin("0.01") BigDecimal priceAmount,
                @NotBlank String currency
) {}