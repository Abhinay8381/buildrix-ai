package com.abhinay.buildrix_ai.entity;

import com.abhinay.buildrix_ai.enums.PreviewStatus;
import lombok.*;

import java.time.Instant;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Preview extends BaseEntity{

    private Project project;
    private String namespace;
    private String podName;
    private String previewUrl;
    private Instant startedAt;
    private Instant terminatedAt;
    private PreviewStatus status;
}
