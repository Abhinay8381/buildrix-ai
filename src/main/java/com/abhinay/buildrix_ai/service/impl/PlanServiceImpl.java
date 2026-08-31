package com.abhinay.buildrix_ai.service.impl;

import com.abhinay.buildrix_ai.dto.billing.plan.PlanResponse;
import com.abhinay.buildrix_ai.service.PlanService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanServiceImpl implements PlanService {

    @Override
    public List<PlanResponse> getAllActivePlans() {
        return List.of();
    }
}
