package com.abhinay.buildrix_ai.dto.billing.subscription;

import com.abhinay.buildrix_ai.dto.billing.plan.PlanResponse;
import com.abhinay.buildrix_ai.enums.SubscriptionStatus;

import java.time.Instant;

public record SubscriptionResponse(
        PlanResponse plan,
        SubscriptionStatus status,
        Instant currentPeriodEnd,
        Long tokensUsedThisCycle
) {
}
