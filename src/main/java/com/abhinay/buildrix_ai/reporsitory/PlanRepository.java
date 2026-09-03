package com.abhinay.buildrix_ai.reporsitory;

import com.abhinay.buildrix_ai.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlanRepository extends JpaRepository<Plan, UUID> {
}
