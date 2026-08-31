package com.abhinay.buildrix_ai.service;

import com.abhinay.buildrix_ai.dto.project.file.FileContentResponse;
import com.abhinay.buildrix_ai.dto.project.file.FileNode;

import java.util.List;
import java.util.UUID;

public interface FileService {
    List<FileNode> getProjectFileTree(UUID userId, UUID projectId);

    FileContentResponse getFileContent(UUID userId, UUID projectId, String filePath);
}
