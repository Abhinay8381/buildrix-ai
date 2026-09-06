package com.abhinay.buildrix_ai.controller;


import com.abhinay.buildrix_ai.dto.chat.ChatRequest;
import com.abhinay.buildrix_ai.service.AiGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final AiGenerationService aiGenerationService;

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    private Flux<ServerSentEvent<String>> streamChat(@RequestBody ChatRequest chatRequest){

        return aiGenerationService.streamResponse(chatRequest.message(), chatRequest.projectId())
                .map(message -> ServerSentEvent.<String>builder()
                        .data(message)
                        .build());
    }
}
