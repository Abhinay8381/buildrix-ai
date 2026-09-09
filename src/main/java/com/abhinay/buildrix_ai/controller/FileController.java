package com.abhinay.buildrix_ai.controller;

import com.abhinay.buildrix_ai.dto.project.file.FileContentResponse;
import com.abhinay.buildrix_ai.dto.project.file.FileNode;
import com.abhinay.buildrix_ai.dto.project.file.FileTreeResponse;
import com.abhinay.buildrix_ai.service.ProjectFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/projects/{projectId}/files")
@RequiredArgsConstructor
public class FileController {

    private final ProjectFileService projectFileService;
    private static final UUID userId = UUID.randomUUID();

    @GetMapping
    public ResponseEntity<FileTreeResponse> getProjectFiles(@PathVariable UUID projectId){
        return ResponseEntity.ok(projectFileService.getProjectFileTree(projectId));
    }

    @GetMapping("/content")
    public ResponseEntity<FileContentResponse> downloadFile(@PathVariable UUID projectId,
                                                            @RequestParam String path){
        return ResponseEntity.ok(projectFileService.getFileContent(projectId, path ));
    }
}
