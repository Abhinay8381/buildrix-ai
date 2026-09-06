package com.abhinay.buildrix_ai.entity;

import com.abhinay.buildrix_ai.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "subscriptions")
public class Subscription extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    String gatewaySubscriptionId;

    private Instant currentPeriodStart;
    private Instant currentPeriodEnd;

    @Builder.Default
    private Boolean cancelAtPeriodEnd = false;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;
}
