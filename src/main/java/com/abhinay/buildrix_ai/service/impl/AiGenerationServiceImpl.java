package com.abhinay.buildrix_ai.service.impl;

import com.abhinay.buildrix_ai.service.AiGenerationService;
import reactor.core.publisher.Flux;

import java.util.UUID;

public class AiGenerationServiceImpl implements AiGenerationService {
    @Override
    public Flux<String> streamResponse(String message, UUID uuid) {
        return null;
    }
}
