package com.abhinay.buildrix_ai.service;

import com.abhinay.buildrix_ai.dto.billing.plan.PlanResponse;

import java.util.List;

public interface PlanService {
    List<PlanResponse> getAllActivePlans();
}
