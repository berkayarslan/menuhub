package com.menuhub.api.restaurant;

import jakarta.validation.constraints.NotBlank;

public record UpdateRestaurantRequest(
                @NotBlank String name,
                @NotBlank String city,
                @NotBlank String district,
                @NotBlank String address,
                @NotBlank String cuisineType,
                boolean verified
) {
}