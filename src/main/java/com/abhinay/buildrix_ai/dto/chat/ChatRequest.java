package com.abhinay.buildrix_ai.dto.chat;

import java.util.UUID;

public record ChatRequest(
        String message,
        UUID projectId
) {
}
