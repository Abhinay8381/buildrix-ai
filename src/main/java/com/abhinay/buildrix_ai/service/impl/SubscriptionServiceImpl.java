package com.abhinay.buildrix_ai.service.impl;

import com.abhinay.buildrix_ai.dto.billing.subscription.CheckoutRequest;
import com.abhinay.buildrix_ai.dto.billing.subscription.CheckoutResponse;
import com.abhinay.buildrix_ai.dto.billing.subscription.PortalResponse;
import com.abhinay.buildrix_ai.dto.billing.subscription.SubscriptionResponse;
import com.abhinay.buildrix_ai.service.SubscriptionService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {
    @Override
    public PortalResponse openCustomerPortal(UUID usedId) {
        return null;
    }

    @Override
    public CheckoutResponse createCheckout(UUID usedId, CheckoutRequest request) {
        return null;
    }

    @Override
    public SubscriptionResponse getUserSubscription(UUID usedId) {
        return null;
    }
}
