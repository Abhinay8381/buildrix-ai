package com.abhinay.buildrix_ai.entity;

import lombok.*;

import java.time.Instant;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectFile extends BaseEntity{

    private Project project;
    private String path;
    private String minioObjectKey;
    private User createdBy;
    private User updatedBy;
}
