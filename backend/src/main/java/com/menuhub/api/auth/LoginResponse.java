package com.menuhub.api.auth;

public record LoginResponse(
                String token,
                String type
) {
}