package com.abhinay.buildrix_ai.entity;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsageLog extends BaseEntity{

    private User user;
    private Project project;
    private String action;
    private Integer tokensUsed;
    private Integer durationMs;
    private String metadata;
}
