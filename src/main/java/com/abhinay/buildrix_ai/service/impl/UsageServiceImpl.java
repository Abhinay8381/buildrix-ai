package com.abhinay.buildrix_ai.service.impl;

import com.abhinay.buildrix_ai.dto.usage.PlanLimitsResponse;
import com.abhinay.buildrix_ai.dto.usage.UsageTodayResponse;
import com.abhinay.buildrix_ai.service.UsageService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UsageServiceImpl implements UsageService {
    @Override
    public UsageTodayResponse getTodayUsage(UUID userId) {
        return null;
    }

    @Override
    public PlanLimitsResponse getUsageLimits(UUID userId) {
        return null;
    }
}
