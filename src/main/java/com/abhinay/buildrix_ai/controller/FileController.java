package com.abhinay.buildrix_ai.controller;

import com.abhinay.buildrix_ai.dto.project.file.FileContentResponse;
import com.abhinay.buildrix_ai.dto.project.file.FileNode;
import com.abhinay.buildrix_ai.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/projects/{projectId}/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private static final UUID userId = UUID.randomUUID();

    @GetMapping
    public ResponseEntity<List<FileNode>> getProjectFiles(@PathVariable UUID projectId){
        return ResponseEntity.ok(fileService.getProjectFileTree(userId, projectId));
    }

    @GetMapping("/{*path}")
    public ResponseEntity<FileContentResponse> downloadFile(@PathVariable UUID projectId,
                                                            @PathVariable("*path") String filePath){
        return ResponseEntity.ok(fileService.getFileContent(userId, projectId, filePath ));
    }
}
