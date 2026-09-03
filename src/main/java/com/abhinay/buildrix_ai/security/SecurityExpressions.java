package com.abhinay.buildrix_ai.security;

import com.abhinay.buildrix_ai.enums.ProjectPermissions;
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


        private boolean hasPermission(UUID projectId, ProjectPermissions permission){
            UUID userId = authUtil.getCurrentUserId();
            return projectMemberRepository.findProjectRoleByMemberIdAndProjectId(userId, projectId)
                    .map((role) -> role.getPermissions().contains(permission))
                    .orElse(false);
        }
        public boolean canViewProject(UUID projectId){
        return hasPermission(projectId, ProjectPermissions.VIEW);
    }

    public boolean canEditProject(UUID projectId){
        return hasPermission(projectId, ProjectPermissions.EDIT);
    }

    public boolean canDeleteProject(UUID projectId){
        return hasPermission(projectId, ProjectPermissions.DELETE);
    }

    public boolean canViewMembers(UUID projectId){
        return hasPermission(projectId, ProjectPermissions.VIEW_MEMBERS);
    }

    public boolean canManageMembers(UUID projectId){
        return hasPermission(projectId, ProjectPermissions.MANAGE_MEMBERS);
    }
}
