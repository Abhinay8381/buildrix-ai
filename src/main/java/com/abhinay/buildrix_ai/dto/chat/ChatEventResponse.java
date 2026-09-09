package com.abhinay.buildrix_ai.dto.chat;

import com.abhinay.buildrix_ai.enums.ChatEventType;

import java.util.UUID;

public record ChatEventResponse(
        UUID id,
        ChatEventType type,
        Integer sequenceOrder,
        String content,
        String filePath,
        String metadata
) {
}
