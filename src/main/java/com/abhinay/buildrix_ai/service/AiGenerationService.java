package com.abhinay.buildrix_ai.service;

import com.abhinay.buildrix_ai.dto.chat.ChatStreamResponse;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface AiGenerationService {
    Flux<ChatStreamResponse> streamResponse(String message, UUID projectId);
}
