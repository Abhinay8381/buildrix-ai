package com.abhinay.buildrix_ai.service.impl;

import com.abhinay.buildrix_ai.dto.project.file.FileContentResponse;
import com.abhinay.buildrix_ai.dto.project.file.FileNode;
import com.abhinay.buildrix_ai.service.FileService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
    @Override
    public List<FileNode> getProjectFileTree(UUID userId, UUID projectId) {
        return List.of();
    }

    @Override
    public FileContentResponse getFileContent(UUID userId, UUID projectId, String filePath) {
        return null;
    }
}
