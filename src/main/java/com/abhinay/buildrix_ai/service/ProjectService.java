package com.abhinay.buildrix_ai.service;

import com.abhinay.buildrix_ai.dto.project.ProjectRequest;
import com.abhinay.buildrix_ai.dto.project.ProjectResponse;
import com.abhinay.buildrix_ai.dto.project.ProjectSummaryResponse;

import java.util.List;
import java.util.UUID;

public interface ProjectService {
    List<ProjectSummaryResponse> getAllUserProjects();

    ProjectSummaryResponse getProjectById(UUID id);

    ProjectResponse createProject(ProjectRequest projectRequest);

    void softDeleteProject(UUID id);

    ProjectResponse updateProject(UUID id, ProjectRequest projectRequest);
}
