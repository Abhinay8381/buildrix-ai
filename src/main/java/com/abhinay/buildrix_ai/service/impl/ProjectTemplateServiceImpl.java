package com.abhinay.buildrix_ai.service.impl;

import com.abhinay.buildrix_ai.entity.Project;
import com.abhinay.buildrix_ai.entity.ProjectFile;
import com.abhinay.buildrix_ai.entity.ProjectFileRepository;
import com.abhinay.buildrix_ai.exceptions.ResourceNotFoundException;
import com.abhinay.buildrix_ai.reporsitory.ProjectRepository;
import com.abhinay.buildrix_ai.service.ProjectFileService;
import com.abhinay.buildrix_ai.service.ProjectTemplateService;
import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
public class ProjectTemplateServiceImpl implements ProjectTemplateService {

    private final MinioClient minioClient;
    private final ProjectFileRepository projectFileRepository;
    private final ProjectRepository projectRepository;

    @Value("${minio.starter-project-bucket}")
    private String STARTER_PROJECT_BUCKET;

    @Value("${minio.react-vite-tailwind-daisyui-starter}")
    private String STARTER_PROJECT_NAME;

    @Value("${minio.project-bucket}")
    private String PROJECT_BUCKET;

    @Transactional
    @Override
    public void initializeProjectFromTemplate(UUID projectId) {

        Project project = projectRepository.findById(projectId).orElseThrow(()
                -> new ResourceNotFoundException("Project", projectId.toString()));

        try {
            Iterable<Result<Item>> items =   minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(STARTER_PROJECT_BUCKET)
                            .prefix(STARTER_PROJECT_NAME + "/")
                            .recursive(true)
                            .build()
            );

            List<ProjectFile> projectFiles = new ArrayList<>();

            for (Result<Item> result : items){
                Item item = result.get();
                String sourceKey = item.objectName();

                String cleanPath = sourceKey.replaceFirst(STARTER_PROJECT_NAME + "/", "");
                String destinationKey = projectId + "/" + cleanPath;

                minioClient.copyObject(
                        CopyObjectArgs.builder()
                                .bucket(PROJECT_BUCKET)
                                .object(destinationKey)
                                .source(CopySource.builder()
                                        .bucket(STARTER_PROJECT_BUCKET)
                                        .object(sourceKey)
                                        .build())
                                .build()
                );

                ProjectFile pf = ProjectFile.builder()
                        .project(project)
                        .path(cleanPath)
                        .minioObjectKey(destinationKey)
                        .build();

                projectFiles.add(pf);

                projectFileRepository.saveAll(projectFiles);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize project from template", e);
        }
    }
}
