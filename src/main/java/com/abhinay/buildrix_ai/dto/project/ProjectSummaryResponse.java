package com.abhinay.buildrix_ai.dto.project;

import com.abhinay.buildrix_ai.enums.ProjectRole;

import java.time.Instant;
import java.util.UUID;

public record ProjectSummaryResponse(
        UUID id,
        String name,
        Instant createdAt,
        Instant updatedAt,
        ProjectRole role
) {
}
