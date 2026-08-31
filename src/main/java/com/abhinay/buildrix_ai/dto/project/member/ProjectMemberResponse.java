package com.abhinay.buildrix_ai.dto.project.member;

import com.abhinay.buildrix_ai.enums.ProjectRole;

import java.time.Instant;
import java.util.UUID;


public record ProjectMemberResponse(
        UUID userId,
        String name,
        String email,
        Instant invitedAt,
        ProjectRole projectRole
) {
}
