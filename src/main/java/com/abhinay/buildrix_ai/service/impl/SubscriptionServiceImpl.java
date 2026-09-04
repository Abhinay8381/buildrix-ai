package com.abhinay.buildrix_ai.service.impl;

import com.abhinay.buildrix_ai.dto.billing.subscription.CheckoutRequest;
import com.abhinay.buildrix_ai.dto.billing.subscription.CheckoutResponse;
import com.abhinay.buildrix_ai.dto.billing.subscription.PortalResponse;
import com.abhinay.buildrix_ai.dto.billing.subscription.SubscriptionResponse;
import com.abhinay.buildrix_ai.entity.Plan;
import com.abhinay.buildrix_ai.entity.User;
import com.abhinay.buildrix_ai.enums.SubscriptionStatus;
import com.abhinay.buildrix_ai.exceptions.ResourceNotFoundException;
import com.abhinay.buildrix_ai.reporsitory.PlanRepository;
import com.abhinay.buildrix_ai.reporsitory.UserRepository;
import com.abhinay.buildrix_ai.security.AuthUtil;
import com.abhinay.buildrix_ai.service.PaymentProcessor;
import com.abhinay.buildrix_ai.service.SubscriptionService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final UserRepository userRepository;
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

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));

        String checkOutUrl = paymentProcessor.checkout(plan, user);
        return new CheckoutResponse(checkOutUrl);
    }

    @Override
    public SubscriptionResponse getUserSubscription(UUID usedId) {
        return null;
    }

    @Override
    public void activateSubscription(UUID userId, UUID planId, String subscriptionId, String customerId) {

    }

    @Override
    public void updateSubscription(String id, SubscriptionStatus status, Instant periodStart, Instant periodEnd, Boolean cancelAtPeriodEnd, UUID planId) {

    }

    @Override
    public void cancelSubscription(String id) {

    }

    @Override
    public void renewSubscription(String subId, Instant periodStart, Instant periodEnd) {

    }

    @Override
    public void markSubscriptionDue(String subId) {

    }


}
