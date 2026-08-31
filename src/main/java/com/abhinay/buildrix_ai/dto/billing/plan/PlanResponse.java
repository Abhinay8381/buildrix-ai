package com.abhinay.buildrix_ai.dto.billing.plan;

import java.util.UUID;

public record PlanResponse(
         UUID id,
         String name,
         Integer maxProjects,
         Integer maxTokensPerDay,
         Integer maxPreviews,
         Boolean unlimitedAi,
         String features,
         String price
) {
}
