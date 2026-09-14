package com.abhinay.buildrix_ai.reporsitory;

import com.abhinay.buildrix_ai.entity.ProjectFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectFileRepository extends JpaRepository<ProjectFile, UUID> {
    Optional<ProjectFile> findByProjectIdAndPath(UUID projectId, String filePath);

    List<ProjectFile> findByProjectId(UUID projectId);
}
