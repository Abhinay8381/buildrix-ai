package com.abhinay.buildrix_ai.dto.chat;


import com.abhinay.buildrix_ai.enums.MessageRole;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChatResponse(
        UUID id,
        MessageRole role,
        List<ChatEventResponse> events,
        String content,
        Integer tokensUsed,
        Instant createdAt

) {
}
