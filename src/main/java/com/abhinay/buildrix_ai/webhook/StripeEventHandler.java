package com.abhinay.buildrix_ai.webhook;

import com.stripe.model.StripeObject;

import java.util.Map;

public interface StripeEventHandler {

    String getEventType();

    void processEvent(StripeObject stripeObject, Map<String, String> metaData);
}
