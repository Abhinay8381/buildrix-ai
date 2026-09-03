package com.abhinay.buildrix_ai.service.impl;

import com.abhinay.buildrix_ai.dto.billing.subscription.CheckoutRequest;
import com.abhinay.buildrix_ai.dto.billing.subscription.CheckoutResponse;
import com.abhinay.buildrix_ai.dto.billing.subscription.PortalResponse;
import com.abhinay.buildrix_ai.dto.billing.subscription.SubscriptionResponse;
import com.abhinay.buildrix_ai.entity.Plan;
import com.abhinay.buildrix_ai.exceptions.ResourceNotFoundException;
import com.abhinay.buildrix_ai.reporsitory.PlanRepository;
import com.abhinay.buildrix_ai.security.AuthUtil;
import com.abhinay.buildrix_ai.service.PaymentProcessor;
import com.abhinay.buildrix_ai.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final PaymentProcessor paymentProcessor;
    private final AuthUtil authUtil;
    private final PlanRepository planRepository;

    @Override
    public PortalResponse openCustomerPortal() {
        return null;
    }

    @Override
    public CheckoutResponse createCheckout(CheckoutRequest request) {
        Plan plan = planRepository.findById(request.planId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan", request.planId().toString()));

        UUID userId = authUtil.getCurrentUserId();
        String checkOutUrl = paymentProcessor.checkout(plan, userId);
        return new CheckoutResponse(checkOutUrl);
    }

    @Override
    public SubscriptionResponse getUserSubscription(UUID usedId) {
        return null;
    }
}
