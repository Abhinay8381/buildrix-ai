package com.abhinay.buildrix_ai.service;

import com.abhinay.buildrix_ai.dto.project.file.FileContentResponse;
import com.abhinay.buildrix_ai.dto.project.file.FileNode;

import java.util.List;
import java.util.UUID;

public interface ProjectFileService {
    List<FileNode> getProjectFileTree(UUID projectId);

    FileContentResponse getFileContent(UUID userId, UUID projectId, String filePath);

    void saveFile(UUID projectId, String filePath, String content);
}
