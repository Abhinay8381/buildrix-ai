package com.abhinay.buildrix_ai.controller;

import com.abhinay.buildrix_ai.dto.usage.PlanLimitsResponse;
import com.abhinay.buildrix_ai.dto.usage.UsageTodayResponse;
import com.abhinay.buildrix_ai.service.PlanService;
import com.abhinay.buildrix_ai.service.UsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/usage")
public class UsageController {

    private final UsageService usageService;
    private static final UUID userId = UUID.randomUUID();

    @GetMapping("/today")
    public ResponseEntity<UsageTodayResponse> getTodayUsage(){
        return ResponseEntity.ok(usageService.getTodayUsage(userId));
    }

    @GetMapping("/limits")
    public ResponseEntity<PlanLimitsResponse> getUsageLimits(){
        return ResponseEntity.ok(usageService.getUsageLimits(userId));
    }


}
