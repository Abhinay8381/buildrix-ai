package com.abhinay.buildrix_ai.entity;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Plan {

    private UUID id;
    private String name;
    private String stripePriceId;
    private Integer maxProjects;
    private Integer maxTokensPerDay;
    private Integer maxPreviews;
    private Boolean unlimitedAi;
    private String features;
    private Boolean active;
}
