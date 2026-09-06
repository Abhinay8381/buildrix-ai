package com.abhinay.buildrix_ai.mapper;

import com.abhinay.buildrix_ai.dto.billing.plan.PlanResponse;
import com.abhinay.buildrix_ai.entity.Plan;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PlanMapper {

    PlanResponse toResponse(Plan plan);
}
