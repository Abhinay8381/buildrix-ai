package com.abhinay.buildrix_ai.dto.billing.subscription;

import com.abhinay.buildrix_ai.dto.billing.plan.PlanResponse;

import java.time.Instant;

public record SubscriptionResponse(
        PlanResponse response,
        String status,
        Instant periodEnd,
        Long tokensUsedThisCycle
) {
}
