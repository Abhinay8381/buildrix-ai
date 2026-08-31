package com.abhinay.buildrix_ai.entity;

import com.abhinay.buildrix_ai.enums.SubscriptionStatus;
import lombok.*;

import java.time.Instant;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Subscription extends BaseEntity{

    private User user;
    private Plan plan;
    private String stripeSubscriptionId;
    private Instant currentPeriodStart;
    private Instant currentPeriodEnd;
    private Boolean cancelAtPeriodEnd;
    private SubscriptionStatus status;
}
