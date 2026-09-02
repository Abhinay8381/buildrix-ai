package com.abhinay.buildrix_ai.service;

import com.abhinay.buildrix_ai.dto.project.member.InviteMemberRequest;
import com.abhinay.buildrix_ai.dto.project.member.ProjectMemberResponse;
import com.abhinay.buildrix_ai.dto.project.member.UpdateProjectMemberRequest;

import java.util.List;
import java.util.UUID;

public interface ProjectMemberService {
    List<ProjectMemberResponse> getAllProjectMembers(UUID projectId);

    ProjectMemberResponse inviteMember(UUID projectId, InviteMemberRequest request);

    ProjectMemberResponse updateMemberRole(UUID projectId, UUID memberId, UpdateProjectMemberRequest request);

    void removeMember(UUID projectId, UUID memberId);
}
