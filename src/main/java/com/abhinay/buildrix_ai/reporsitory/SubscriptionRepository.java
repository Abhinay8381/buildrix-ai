package com.abhinay.buildrix_ai.reporsitory;

import com.abhinay.buildrix_ai.entity.Subscription;
import com.abhinay.buildrix_ai.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    Optional<Subscription> findByUserIdAndStatusIn(UUID userId, Set<SubscriptionStatus> active);

    boolean existsByGatewaySubscriptionId(String subscriptionId);

    Optional<Subscription> findByGatewaySubscriptionId(String gatewaySubId);
}
