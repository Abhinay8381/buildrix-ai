package com.abhinay.buildrix_ai.service.impl;

import com.abhinay.buildrix_ai.dto.project.ProjectRequest;
import com.abhinay.buildrix_ai.dto.project.ProjectResponse;
import com.abhinay.buildrix_ai.dto.project.ProjectSummaryResponse;
import com.abhinay.buildrix_ai.entity.Project;
import com.abhinay.buildrix_ai.entity.User;
import com.abhinay.buildrix_ai.exceptions.ResourceNotFoundException;
import com.abhinay.buildrix_ai.mapper.ProjectMapper;
import com.abhinay.buildrix_ai.reporsitory.ProjectRepository;
import com.abhinay.buildrix_ai.reporsitory.UserRepository;
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

    @Override
    public List<ProjectSummaryResponse> getAllUserProjects(UUID ownerId) {
       return projectMapper.toProjectSummaryResponseList(
               projectRepository.findAllAccessibleByUser(ownerId));

    }

    @Override
    public ProjectResponse getProjectById(UUID ownerId, UUID id) {
        Project project = getAccessibleProjectById(ownerId, id);
        return projectRepository.findAccessibleProjectById(ownerId, id)
                .map(projectMapper::toProjectResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id.toString()));
    }


    @Transactional
    @Override
    public ProjectResponse createProject(ProjectRequest projectRequest, UUID userId) {
        User owner = findUserById(userId);
        Project project = Project.builder()
                .name(projectRequest.name())
                .owner(owner)
                .build();
        projectRepository.save(project);
        return projectMapper.toProjectResponse(project);
    }

    @Transactional
    @Override
    public void softDeleteProject(UUID ownerId, UUID id) {
        Project project = getAccessibleProjectById(ownerId, id);
        if(!project.getOwner().getId().equals(ownerId))
            throw new RuntimeException("User not authorized to updated this project");
       project.setDeletedAt(Instant.now());
    }

    @Transactional
    @Override
    public ProjectResponse updateProject(UUID ownerId, UUID id, ProjectRequest projectRequest) {
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
