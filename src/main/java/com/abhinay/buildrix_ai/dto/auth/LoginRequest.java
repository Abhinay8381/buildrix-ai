package com.abhinay.buildrix_ai.dto.auth;

public record LoginRequest(
        String email,
        String password
) {
}
