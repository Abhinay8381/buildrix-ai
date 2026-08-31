package com.abhinay.buildrix_ai.service;

import com.abhinay.buildrix_ai.dto.usage.PlanLimitsResponse;
import com.abhinay.buildrix_ai.dto.usage.UsageTodayResponse;

import java.util.UUID;

public interface UsageService {
    UsageTodayResponse getTodayUsage(UUID userId);

    PlanLimitsResponse getUsageLimits(UUID userId);
}
