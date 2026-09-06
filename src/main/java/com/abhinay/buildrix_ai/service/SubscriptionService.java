package com.abhinay.buildrix_ai.service;

import com.abhinay.buildrix_ai.dto.billing.subscription.CheckoutRequest;
import com.abhinay.buildrix_ai.dto.billing.subscription.CheckoutResponse;
import com.abhinay.buildrix_ai.dto.billing.subscription.PortalResponse;
import com.abhinay.buildrix_ai.dto.billing.subscription.SubscriptionResponse;
import com.abhinay.buildrix_ai.enums.SubscriptionStatus;

import java.time.Instant;
import java.util.UUID;

public interface SubscriptionService {
    PortalResponse openCustomerPortal();

    CheckoutResponse createCheckout(CheckoutRequest request);

    SubscriptionResponse getUserSubscription();


    void activateSubscription(UUID userId, UUID planId, String subscriptionId, String customerId);

    void updateSubscription(String id, SubscriptionStatus status, Instant periodStart, Instant periodEnd, Boolean cancelAtPeriodEnd, UUID planId);

    void cancelSubscription(String id);

    void renewSubscription(String subId, Instant periodStart, Instant periodEnd, String customerEmail, String stripePriceId);

    void markSubscriptionDue(String subId);

    boolean canCreateProject();
}
