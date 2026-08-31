package com.abhinay.buildrix_ai.dto.project.member;

import com.abhinay.buildrix_ai.enums.ProjectRole;

public record UpdateProjectMemberRequest(
        ProjectRole role
) {
}
