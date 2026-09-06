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

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomerSubscriptionUpdatedHandler implements StripeEventHandler {

    private final PlanRepository planRepository;
    private final SubscriptionService subscriptionService;

    @Override
    public String getEventType() {
        return "customer.subscription.updated";
    }

    @Override
    public void processEvent(StripeObject stripeObject, Map<String, String> metaData) {
        Subscription subscription = (Subscription) stripeObject;

        if(subscription == null){
            log.error("Stripe subscription is null for event: customer.subscription.deleted");
            return;
        }
        SubscriptionStatus status = mapStripeStausToEnum(subscription.getStatus());
        if(status == null){
            log.warn("Unmapped Stripe subscription status: {}", subscription.getStatus());
            return;
        }

        SubscriptionItem item = subscription.getItems().getData().getFirst();
        Instant periodStart = toInstant(item.getCurrentPeriodStart());
        Instant periodEnd = toInstant(item.getCurrentPeriodEnd());

        UUID planId = resolvePlanId(item.getPrice());

        subscriptionService.updateSubscription(
                subscription.getId(), status, periodStart, periodEnd,
                subscription.getCancelAtPeriodEnd(), planId
        );
    }

    private UUID resolvePlanId(Price price) {
        return planRepository.findByStripePriceId(price.getId())
                .map(Plan::getId)
                .orElse(null);
    }

    private Instant toInstant(Long epoch) {
        return epoch == null ? null: Instant.ofEpochSecond(epoch);
    }

    private SubscriptionStatus mapStripeStausToEnum(String status){
        return switch (status) {
            case "active" -> SubscriptionStatus.ACTIVE;
            case "trailing" -> SubscriptionStatus.TRAILING;
            case "past_due", "unpaid", "paused", "incomplete_expired" -> SubscriptionStatus.PAST_DUE;
            case "canceled" -> SubscriptionStatus.CANCELED;
            case "incomplete" -> SubscriptionStatus.INCOMPLETE;
            default -> {
                log.warn("Unmapped Stripe status: {}", status);
                yield null;
            }
        };
    }
}
