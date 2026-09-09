package com.abhinay.buildrix_ai.dto.auth;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        UserProfileResponse user
) {
}

