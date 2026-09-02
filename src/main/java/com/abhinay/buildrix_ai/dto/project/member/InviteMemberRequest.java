package com.abhinay.buildrix_ai.dto.project.member;

import com.abhinay.buildrix_ai.enums.ProjectRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InviteMemberRequest(
        @Email @NotNull String email,
        @NotNull ProjectRole role
) {
}
