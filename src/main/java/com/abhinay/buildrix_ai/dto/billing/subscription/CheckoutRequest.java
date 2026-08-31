package com.abhinay.buildrix_ai.dto.billing.subscription;

import java.util.UUID;

public record CheckoutRequest(
        UUID planId
) {
}
