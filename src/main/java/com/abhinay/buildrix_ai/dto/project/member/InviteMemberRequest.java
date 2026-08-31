package com.abhinay.buildrix_ai.dto.project.member;

import com.abhinay.buildrix_ai.enums.ProjectRole;

public record InviteMemberRequest(
        String email,
        ProjectRole role
) {
}
