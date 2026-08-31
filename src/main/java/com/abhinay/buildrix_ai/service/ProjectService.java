package com.abhinay.buildrix_ai.service;

import com.abhinay.buildrix_ai.dto.project.ProjectRequest;
import com.abhinay.buildrix_ai.dto.project.ProjectResponse;
import com.abhinay.buildrix_ai.dto.project.ProjectSummaryResponse;

import java.util.List;
import java.util.UUID;

public interface ProjectService {
    List<ProjectSummaryResponse> getAllUserProjects(UUID userId);

    ProjectResponse getProjectById(UUID userId, UUID id);

    ProjectResponse createProject(ProjectRequest projectRequest, UUID userId);

    void softDeleteProject(UUID userId, UUID id);

    ProjectResponse updateProject(UUID userId, UUID id, ProjectRequest projectRequest);
}
