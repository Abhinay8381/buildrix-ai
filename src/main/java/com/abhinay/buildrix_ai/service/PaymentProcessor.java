package com.abhinay.buildrix_ai.service;

import com.abhinay.buildrix_ai.dto.billing.subscription.CheckoutRequest;
import com.abhinay.buildrix_ai.dto.billing.subscription.CheckoutResponse;
import com.abhinay.buildrix_ai.dto.billing.subscription.PortalResponse;
import com.abhinay.buildrix_ai.entity.Plan;
import com.abhinay.buildrix_ai.entity.User;
import com.stripe.model.StripeObject;

import java.util.Map;
import java.util.UUID;

public interface PaymentProcessor {

    public PortalResponse openCustomerPortal();

    public String checkout(Plan plan, User user);

}
