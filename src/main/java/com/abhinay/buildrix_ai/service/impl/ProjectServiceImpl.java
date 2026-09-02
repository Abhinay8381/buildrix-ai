package com.abhinay.buildrix_ai.service.impl;

import com.abhinay.buildrix_ai.dto.project.ProjectRequest;
import com.abhinay.buildrix_ai.dto.project.ProjectResponse;
import com.abhinay.buildrix_ai.dto.project.ProjectSummaryResponse;
import com.abhinay.buildrix_ai.entity.Project;
import com.abhinay.buildrix_ai.entity.ProjectMember;
import com.abhinay.buildrix_ai.entity.ProjectMemberId;
import com.abhinay.buildrix_ai.entity.User;
import com.abhinay.buildrix_ai.enums.ProjectRole;
import com.abhinay.buildrix_ai.exceptions.ResourceNotFoundException;
import com.abhinay.buildrix_ai.mapper.ProjectMapper;
import com.abhinay.buildrix_ai.reporsitory.ProjectMemberRepository;
import com.abhinay.buildrix_ai.reporsitory.ProjectRepository;
import com.abhinay.buildrix_ai.reporsitory.UserRepository;
import com.abhinay.buildrix_ai.security.AuthUtil;
import com.abhinay.buildrix_ai.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    public List<ProjectSummaryResponse> getAllUserProjects() {
        UUID userId = authUtil.getCurrentUserId();
        return projectMapper.toProjectSummaryResponseList(
                projectRepository.findAllAccessibleByUser(userId));
    }

    @Override
    public ProjectResponse getProjectById( UUID id) {
        UUID ownerId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(ownerId, id);
        return projectRepository.findAccessibleProjectById(ownerId, id)
                .map(projectMapper::toProjectResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id.toString()));
    }


    @Transactional
    @Override
    public ProjectResponse createProject(ProjectRequest projectRequest) {
        UUID userId = authUtil.getCurrentUserId();
        Project project = Project.builder()
                .name(projectRequest.name())
                .build();
        project = projectRepository.save(project);

        ProjectMemberId projectMemberId = new ProjectMemberId(project.getId(), userId);
        ProjectMember projectMember = ProjectMember.builder()
                .id(projectMemberId)
                .role(ProjectRole.OWNER)
                .invitedAt(Instant.now())
                .acceptedAt(Instant.now())
                .build();
        projectMemberRepository.save(projectMember);
        return projectMapper.toProjectResponse(project);
    }

    @Transactional
    @Override
    public void softDeleteProject(UUID id) {
        UUID ownerId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(ownerId, id);
       project.setDeletedAt(Instant.now());
    }

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
