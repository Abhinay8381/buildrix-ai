package com.abhinay.buildrix_ai.controller;

import com.abhinay.buildrix_ai.dto.billing.plan.PlanResponse;
import com.abhinay.buildrix_ai.dto.billing.subscription.CheckoutRequest;
import com.abhinay.buildrix_ai.dto.billing.subscription.CheckoutResponse;
import com.abhinay.buildrix_ai.dto.billing.subscription.PortalResponse;
import com.abhinay.buildrix_ai.dto.billing.subscription.SubscriptionResponse;
import com.abhinay.buildrix_ai.service.PlanService;
import com.abhinay.buildrix_ai.service.SubscriptionService;
import com.abhinay.buildrix_ai.webhook.WebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class BillingController {


    private final SubscriptionService subscriptionService;
    private final WebhookService webhookService;
    private final PlanService planService;
    private static final UUID userId = UUID.randomUUID();

    @GetMapping("/api/v1/plans")
    public ResponseEntity<List<PlanResponse>> getAllActivePlans(){
        return ResponseEntity.ok(planService.getAllActivePlans());
    }

    @GetMapping("/api/v1/me/subscription")
    public ResponseEntity<SubscriptionResponse> getMySubscription(){
        return ResponseEntity.ok(subscriptionService.getUserSubscription());
    }

    @PostMapping("/api/v1/payment/checkout")
    public ResponseEntity<CheckoutResponse> createCheckout(@RequestBody CheckoutRequest request){
        return ResponseEntity.status(200)
                .body(subscriptionService.createCheckout(request));
    }

    @PostMapping("/api/v1/payment/portal")
    public ResponseEntity<PortalResponse> openCustomerPortal(){
        return ResponseEntity.ok(subscriptionService.openCustomerPortal());
    }

    @PostMapping("webhooks/payment")
    public ResponseEntity<Void> paymentWebhook(@RequestBody String payload,
                                               @RequestHeader("Stripe-Signature") String sigHeader){
        webhookService.processWebhook(payload, sigHeader);

        return ResponseEntity.ok().build();
    }
}
