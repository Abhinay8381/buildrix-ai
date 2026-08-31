package com.abhinay.buildrix_ai.dto.usage;

public record PlanLimitsResponse (
        String planName,
        Integer maxTokensPerDay,
        Integer maxProjects,
        Boolean unlimitedAi
){
}
