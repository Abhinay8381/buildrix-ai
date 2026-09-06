package com.abhinay.buildrix_ai.mapper;

import com.abhinay.buildrix_ai.dto.billing.subscription.SubscriptionResponse;
import com.abhinay.buildrix_ai.entity.Subscription;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SubscriptionMapper {

    SubscriptionResponse toResponse(Subscription subscription);
}
