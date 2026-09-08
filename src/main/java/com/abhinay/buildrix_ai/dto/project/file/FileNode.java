package com.abhinay.buildrix_ai.dto.project.file;

import org.jspecify.annotations.NonNull;

public record FileNode(
        String path
) {

    @Override
    public @NonNull String toString(){
        return path;
    }
}
