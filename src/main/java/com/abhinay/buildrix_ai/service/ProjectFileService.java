package com.abhinay.buildrix_ai.service;

import com.abhinay.buildrix_ai.dto.project.file.FileContentResponse;
import com.abhinay.buildrix_ai.dto.project.file.FileNode;
import com.abhinay.buildrix_ai.dto.project.file.FileTreeResponse;

import java.util.List;
import java.util.UUID;

public interface ProjectFileService {
    FileTreeResponse getProjectFileTree(UUID projectId);

    FileContentResponse getFileContent(UUID projectId, String filePath);

    void saveFile(UUID projectId, String filePath, String content);
}
