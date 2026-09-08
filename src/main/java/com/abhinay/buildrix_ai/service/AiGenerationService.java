package com.abhinay.buildrix_ai.service;

import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface AiGenerationService {
    Flux<String> streamResponse(String message, UUID projectId);
}
