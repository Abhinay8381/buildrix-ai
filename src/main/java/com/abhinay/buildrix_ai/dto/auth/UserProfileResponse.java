package com.abhinay.buildrix_ai.dto.auth;

import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String name,
        String avatarUrl,
        String email
) {
}
