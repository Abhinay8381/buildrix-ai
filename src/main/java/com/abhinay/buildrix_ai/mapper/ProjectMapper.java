package com.abhinay.buildrix_ai.mapper;

import com.abhinay.buildrix_ai.dto.project.ProjectResponse;
import com.abhinay.buildrix_ai.dto.project.ProjectSummaryResponse;
import com.abhinay.buildrix_ai.entity.Project;
import com.abhinay.buildrix_ai.enums.ProjectRole;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProjectMapper {

    ProjectResponse toProjectResponse(Project project);
    ProjectSummaryResponse toProjectSummaryResponse(Project project, ProjectRole role);
    List<ProjectSummaryResponse> toProjectSummaryResponseList(List<Project> project);
}
