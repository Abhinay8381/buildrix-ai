package com.abhinay.buildrix_ai.service.impl;

import com.abhinay.buildrix_ai.dto.project.member.InviteMemberRequest;
import com.abhinay.buildrix_ai.dto.project.member.ProjectMemberResponse;
import com.abhinay.buildrix_ai.dto.project.member.UpdateProjectMemberRequest;
import com.abhinay.buildrix_ai.entity.Project;
import com.abhinay.buildrix_ai.entity.ProjectMember;
import com.abhinay.buildrix_ai.entity.ProjectMemberId;
import com.abhinay.buildrix_ai.entity.User;
import com.abhinay.buildrix_ai.exceptions.ResourceNotFoundException;
import com.abhinay.buildrix_ai.mapper.ProjectMemberMapper;
import com.abhinay.buildrix_ai.reporsitory.ProjectMemberRepository;
import com.abhinay.buildrix_ai.reporsitory.ProjectRepository;
import com.abhinay.buildrix_ai.reporsitory.UserRepository;
import com.abhinay.buildrix_ai.security.AuthUtil;
import com.abhinay.buildrix_ai.service.ProjectMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProjectMemberServiceImpl implements ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberMapper projectMemberMapper;
    private final UserRepository userRepository;
    private final AuthUtil authUtil;

    @Override
    public List<ProjectMemberResponse> getAllProjectMembers(UUID projectId) {
        UUID userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(userId, projectId);
        return projectMemberRepository.findById_ProjectId(projectId)
                .stream()
                .map(projectMemberMapper::toProjectMemberResponseFromProjectMember)
                .toList();
    }

    @Transactional
    @Override
    public ProjectMemberResponse inviteMember(UUID projectId, InviteMemberRequest request) {
        UUID userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(userId, projectId);

        User invitee = userRepository.findByEmail(request.email()).orElseThrow(() ->
                new ResourceNotFoundException("User", request.email()));

        if(userId.equals(invitee.getId()))
            throw new RuntimeException("Cannot invite yourself");

        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, invitee.getId());
        if(projectMemberRepository.existsById(projectMemberId))
            throw new RuntimeException(
                    "User is already a project member"
            );

        ProjectMember projectMember = ProjectMember.builder().id(projectMemberId)
                .role(request.role())
                .invitedAt(Instant.now())
                .project(project)
                .member(invitee)
                .build();
        projectMemberRepository.save(projectMember);
        return projectMemberMapper.toProjectMemberResponseFromProjectMember(projectMember);
    }

    @Transactional
    @Override
    public ProjectMemberResponse updateMemberRole(UUID projectId, UUID memberId, UpdateProjectMemberRequest request) {
        UUID userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(userId, projectId);

        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, memberId);
        ProjectMember projectMember = projectMemberRepository.findById(projectMemberId)
                .orElseThrow(() -> new ResourceNotFoundException("Project Member", memberId.toString()));

        projectMember.setRole(request.role());
        projectMemberRepository.save(projectMember);
        return projectMemberMapper.toProjectMemberResponseFromProjectMember(projectMember);
    }

    @Transactional
    @Override
    public void removeMember(UUID projectId, UUID memberId) {
        UUID userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(userId, projectId);


        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, memberId);
        if(!projectMemberRepository.existsById(projectMemberId))
            throw new RuntimeException(
                    "User is not a project member"
            );
        projectMemberRepository.deleteById(projectMemberId);
    }

    private Project getAccessibleProjectById(UUID ownerId, UUID id) {
        return projectRepository.findAccessibleProjectById(ownerId, id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id.toString()));
    }
}
