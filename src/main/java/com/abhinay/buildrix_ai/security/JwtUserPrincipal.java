package com.abhinay.buildrix_ai.security;

import java.util.UUID;

public record JwtUserPrincipal(
        String email,
        UUID userId
) {
}
