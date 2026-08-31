package com.abhinay.buildrix_ai.dto.project.file;

import java.time.Instant;

public record FileNode(
        String path,
        String type,
        Long fileSize,
        Instant updatedAt
) {
}
