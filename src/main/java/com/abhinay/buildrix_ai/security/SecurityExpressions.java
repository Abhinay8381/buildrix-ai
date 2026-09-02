package com.abhinay.buildrix_ai.security;

import com.abhinay.buildrix_ai.enums.ProjectRole;
import com.abhinay.buildrix_ai.reporsitory.ProjectMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component("security")
public class SecurityExpressions {

    private final AuthUtil authUtil;
    private final ProjectMemberRepository projectMemberRepository;

    public boolean canViewProject(UUID projectId){
        UUID userId = authUtil.getCurrentUserId();
        return projectMemberRepository.findProjectRoleByMemberIdAndProjectId(userId, projectId)
                .map((role) -> role.equals(ProjectRole.OWNER) || role.equals(ProjectRole.VIEWER)
                || role.equals(ProjectRole.EDITOR))
                .orElse(false);
    }

    public boolean canEditProject(UUID projectId){
        UUID userId = authUtil.getCurrentUserId();
        return projectMemberRepository.findProjectRoleByMemberIdAndProjectId(userId, projectId)
                .map((role) -> role.equals(ProjectRole.OWNER)
                        || role.equals(ProjectRole.EDITOR))
                .orElse(false);
    }
}
