package com.abhinay.buildrix_ai.service;

import com.abhinay.buildrix_ai.dto.billing.subscription.CheckoutRequest;
import com.abhinay.buildrix_ai.dto.billing.subscription.CheckoutResponse;
import com.abhinay.buildrix_ai.dto.billing.subscription.PortalResponse;
import com.abhinay.buildrix_ai.dto.billing.subscription.SubscriptionResponse;

import java.util.UUID;

public interface SubscriptionService {
    PortalResponse openCustomerPortal(UUID usedId);

    CheckoutResponse createCheckout(UUID usedId, CheckoutRequest request);

    SubscriptionResponse getUserSubscription(UUID usedId);
}
