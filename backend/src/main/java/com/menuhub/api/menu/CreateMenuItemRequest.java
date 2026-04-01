package com.menuhub.api.menu;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateMenuItemRequest(
                @NotBlank String category,
                @NotBlank String name,
                String descriptionText,
                @NotNull Double priceAmount,
                @NotBlank String currency
) {
}