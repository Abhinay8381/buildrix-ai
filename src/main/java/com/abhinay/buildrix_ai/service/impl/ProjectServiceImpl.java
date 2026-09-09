package com.abhinay.buildrix_ai.service.impl;

import com.abhinay.buildrix_ai.dto.project.ProjectRequest;
import com.abhinay.buildrix_ai.dto.project.ProjectResponse;
import com.abhinay.buildrix_ai.dto.project.ProjectSummaryResponse;
import com.abhinay.buildrix_ai.entity.Project;
import com.abhinay.buildrix_ai.entity.ProjectMember;
import com.abhinay.buildrix_ai.entity.ProjectMemberId;
import com.abhinay.buildrix_ai.entity.User;
import com.abhinay.buildrix_ai.enums.ProjectRole;
import com.abhinay.buildrix_ai.exceptions.BadRequestException;
import com.abhinay.buildrix_ai.exceptions.ResourceNotFoundException;
import com.abhinay.buildrix_ai.mapper.ProjectMapper;
import com.abhinay.buildrix_ai.reporsitory.ProjectMemberRepository;
import com.abhinay.buildrix_ai.reporsitory.ProjectRepository;
import com.abhinay.buildrix_ai.reporsitory.UserRepository;
import com.abhinay.buildrix_ai.security.AuthUtil;
import com.abhinay.buildrix_ai.service.ProjectService;
import com.abhinay.buildrix_ai.service.ProjectTemplateService;
import com.abhinay.buildrix_ai.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProjectServiceImpl implements ProjectService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final ProjectMemberRepository projectMemberRepository;
    private final AuthUtil authUtil;
    private final SubscriptionService subscriptionService;
    private final ProjectTemplateService projectTemplateService;

    @Override
    public List<ProjectSummaryResponse> getAllUserProjects() {
        UUID userId = authUtil.getCurrentUserId();
        return projectRepository.findAllAccessibleByUser(userId)
                .stream()
                .map(p -> projectMapper.toProjectSummaryResponse(
                        p.getProject(), p.getRole()
                )).toList();
    }

    @PreAuthorize("@security.canViewProject(#id)")
    @Override
    public ProjectSummaryResponse getProjectById( UUID id) {
        UUID ownerId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(ownerId, id);
        return projectRepository.findAccessibleProjectByIdWithRole(ownerId, id)
                .map(p -> projectMapper.toProjectSummaryResponse(p.getProject(), p.getRole()))
                .orElseThrow(() -> new BadRequestException("Project not found"));
    }


    @Transactional
    @Override
    public ProjectResponse createProject(ProjectRequest projectRequest) {
        if(!subscriptionService.canCreateProject()){
            throw new BadRequestException("Cannot create project. Project limit Exceeded. Please upgrade you plan to create more projects.");
        }
        UUID userId = authUtil.getCurrentUserId();
        Project project = Project.builder()
                .name(projectRequest.name())
                .build();
        project = projectRepository.save(project);
        User user = userRepository.getReferenceById(userId);

        ProjectMemberId projectMemberId = new ProjectMemberId(project.getId(), userId);
        ProjectMember projectMember = ProjectMember.builder()
                .id(projectMemberId)
                .project(project)
                .member(user)
                .role(ProjectRole.OWNER)
                .invitedAt(Instant.now())
                .acceptedAt(Instant.now())
                .build();
        projectMemberRepository.save(projectMember);

        projectTemplateService.initializeProjectFromTemplate(project.getId());
        return projectMapper.toProjectResponse(project);
    }

    @Transactional
    @Override
    @PreAuthorize("@security.canDeleteProject(#id)")
    public void softDeleteProject(UUID id) {
        UUID ownerId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(ownerId, id);
       project.setDeletedAt(Instant.now());
    }

    @PreAuthorize("@security.canEditProject(#id)")
    @Transactional
    @Override
    public ProjectResponse updateProject(UUID id, ProjectRequest projectRequest) {
        UUID ownerId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(ownerId, id);
        project.setName(projectRequest.name());
        projectRepository.save(project);
        return projectMapper.toProjectResponse(project);
    }

    private User findUserById(UUID id){
       return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id.toString()));
    }

    private Project getAccessibleProjectById(UUID ownerId, UUID id) {
        return projectRepository.findAccessibleProjectById(ownerId, id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id.toString()));
    }
}
