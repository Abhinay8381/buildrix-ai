package com.abhinay.buildrix_ai.dto.auth;

public record SignUpRequest(
        String name,
        String email,
        String password
) {
}
