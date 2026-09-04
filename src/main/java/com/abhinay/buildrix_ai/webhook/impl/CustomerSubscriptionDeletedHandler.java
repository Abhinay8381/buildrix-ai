package com.abhinay.buildrix_ai.webhook.impl;

import com.abhinay.buildrix_ai.entity.Plan;
import com.abhinay.buildrix_ai.enums.SubscriptionStatus;
import com.abhinay.buildrix_ai.reporsitory.PlanRepository;
import com.abhinay.buildrix_ai.service.SubscriptionService;
import com.abhinay.buildrix_ai.webhook.StripeEventHandler;
import com.stripe.model.Price;
import com.stripe.model.StripeObject;
import com.stripe.model.Subscription;
import com.stripe.model.SubscriptionItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomerSubscriptionDeletedHandler implements StripeEventHandler {

    private final SubscriptionService subscriptionService;

    @Override
    public String getEventType() {
        return "customer.subscription.deleted";
    }

    @Override
    public void processEvent(StripeObject stripeObject, Map<String, String> metaData) {
        Subscription subscription = (Subscription) stripeObject;

        if(subscription == null){
            log.error("Stripe subscription is null for event: customer.subscription.deleted");
            return;
        }

        subscriptionService.cancelSubscription(subscription.getId());
    }


}
