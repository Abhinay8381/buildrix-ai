package com.abhinay.buildrix_ai.dto.project.file;

import java.util.List;

public record FileTreeResponse(
        List<FileNode> files
) {
}
