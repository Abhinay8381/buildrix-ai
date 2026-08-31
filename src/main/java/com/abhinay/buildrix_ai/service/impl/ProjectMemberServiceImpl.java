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

    @Override
    public List<ProjectMemberResponse> getAllProjectMembers(UUID userId, UUID projectId) {
        Project project = getAccessibleProjectById(userId, projectId);
        List<ProjectMemberResponse> projectMembers = new ArrayList<>();
        projectMembers.add(projectMemberMapper.toProjectMemberResponseFromOwner(project.getOwner()));
        projectMembers.addAll(projectMemberRepository.findById_ProjectId(projectId)
                .stream()
                .map(projectMemberMapper::toProjectMemberResponseFromProjectMember)
                .toList());
        return projectMembers;
    }

    @Transactional
    @Override
    public ProjectMemberResponse inviteMember(UUID userId, UUID projectId, InviteMemberRequest request) {
        Project project = getAccessibleProjectById(userId, projectId);

        if(!project.getOwner().getId().equals(userId))
            throw new RuntimeException("Not allowed");

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
    public ProjectMemberResponse updateMemberRole(UUID userId, UUID projectId, UUID memberId, UpdateProjectMemberRequest request) {
        Project project = getAccessibleProjectById(userId, projectId);

        if(!project.getOwner().getId().equals(userId))
            throw new RuntimeException("Not allowed");

        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, memberId);
        ProjectMember projectMember = projectMemberRepository.findById(projectMemberId)
                .orElseThrow(() -> new ResourceNotFoundException("Project Member", memberId.toString()));

        projectMember.setRole(request.role());
        projectMemberRepository.save(projectMember);
        return projectMemberMapper.toProjectMemberResponseFromProjectMember(projectMember);
    }

    @Transactional
    @Override
    public void removeMember(UUID userId, UUID projectId, UUID memberId) {
        Project project = getAccessibleProjectById(userId, projectId);

        if(!project.getOwner().getId().equals(userId))
            throw new RuntimeException("Not allowed");

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
