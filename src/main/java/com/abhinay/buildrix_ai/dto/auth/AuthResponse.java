package com.abhinay.buildrix_ai.dto.auth;

public record AuthResponse(
        String token,
        UserProfileResponse user
) {
}
