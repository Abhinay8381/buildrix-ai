package com.abhinay.buildrix_ai.controller;

import com.abhinay.buildrix_ai.dto.project.ProjectRequest;
import com.abhinay.buildrix_ai.dto.project.ProjectResponse;
import com.abhinay.buildrix_ai.dto.project.ProjectSummaryResponse;
import com.abhinay.buildrix_ai.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private static final UUID userId = UUID.fromString("32aea559-aed5-45c5-bfec-23dcc3ef70c5");

    private final ProjectService projectService;

    @GetMapping()
    public ResponseEntity<List<ProjectSummaryResponse>> getAllProjects(){
        return ResponseEntity.ok(projectService.getAllUserProjects(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProject(@PathVariable UUID id){
        return ResponseEntity.ok(projectService.getProjectById(userId, id));
    }

    @PostMapping()
    public ResponseEntity<ProjectResponse> createProject(@RequestBody  ProjectRequest projectRequest){
        return ResponseEntity.status(201)
                .body(projectService.createProject(projectRequest, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID id){
        projectService.softDeleteProject(userId, id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(@RequestBody  ProjectRequest projectRequest,
                                                         @PathVariable UUID id){
        return ResponseEntity.ok(projectService.updateProject(userId, id, projectRequest));
    }

}
