package com.abhinay.buildrix_ai.service;

import com.abhinay.buildrix_ai.dto.billing.subscription.CheckoutRequest;
import com.abhinay.buildrix_ai.dto.billing.subscription.CheckoutResponse;
import com.abhinay.buildrix_ai.dto.billing.subscription.PortalResponse;
import com.abhinay.buildrix_ai.entity.Plan;

import java.util.UUID;

public interface PaymentProcessor {

    public PortalResponse openCustomerPortal();

    public String checkout(Plan plan, UUID userId);
}
