package com.abhinay.buildrix_ai.dto.project.member;

import com.abhinay.buildrix_ai.enums.ProjectRole;
import jakarta.validation.constraints.NotNull;

public record UpdateProjectMemberRequest(
        @NotNull ProjectRole role
) {
}
