package com.abhinay.buildrix_ai.dto.project;

import com.abhinay.buildrix_ai.dto.auth.UserProfileResponse;

import java.time.Instant;
import java.util.UUID;

public record ProjectResponse(
        UUID id,
        String name,
        Instant createdAt,
        Instant updatedAt,
        UserProfileResponse owner
) {
}
