package com.abhinay.buildrix_ai.service.impl;

import com.abhinay.buildrix_ai.dto.chat.ChatResponse;
import com.abhinay.buildrix_ai.entity.*;
import com.abhinay.buildrix_ai.enums.ChatEventType;
import com.abhinay.buildrix_ai.enums.MessageRole;
import com.abhinay.buildrix_ai.exceptions.ResourceNotFoundException;
import com.abhinay.buildrix_ai.llm.LLMResponseParser;
import com.abhinay.buildrix_ai.mapper.ChatMapper;
import com.abhinay.buildrix_ai.reporsitory.*;
import com.abhinay.buildrix_ai.security.AuthUtil;
import com.abhinay.buildrix_ai.service.ChatService;
import com.abhinay.buildrix_ai.service.ProjectFileService;
import com.abhinay.buildrix_ai.service.UsageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {
    private final ChatEventRepository chatEventRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final LLMResponseParser llmResponseParser;
    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final AuthUtil authUtil;
    private final ChatMapper chatMapper;
    private final ProjectFileService projectFileService;
    private final UsageService usageService;

    public List<ChatResponse> getProjectChatHistory(UUID projectId){
        UUID userId = authUtil.getCurrentUserId();
        ChatSession chatSession = chatSessionRepository.findById(new ChatSessionId(userId, projectId))
                .orElseThrow(() -> new ResourceNotFoundException("Chat Session", projectId + "-" +userId));

        List<ChatMessage> messages = chatMessageRepository.findChatMessagesByChatSession(chatSession);
        return chatMapper.fromListOfChatMessage(messages);
    }

    @Transactional
    @Override
    public ChatSession createChatSessionIfNotExists(UUID projectId, UUID userId) {
        ChatSessionId chatSessionId = new ChatSessionId(userId, projectId);
        ChatSession chatSession = chatSessionRepository.findById(chatSessionId)
                .orElse(null);

        if(chatSession == null){
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new ResourceNotFoundException("Project", projectId.toString()));

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));

            chatSession = ChatSession.builder()
                    .id(chatSessionId)
                    .project(project)
                    .user(user)
                    .build();
            chatSession = chatSessionRepository.save(chatSession);
        }
        return chatSession;
    }

    @Transactional
    @Override
    public void finalizeChats(String userMessage, ChatSession chatSession, String fullText, Long duration, Usage usage) {
        UUID projectId = chatSession.getId().getProjectId();

        if(usage != null)
            usageService.recordTokenUsage(chatSession.getUser().getId(), usage.getTotalTokens());

        chatMessageRepository.save(
                ChatMessage.builder()
                        .chatSession(chatSession)
                        .role(MessageRole.USER)
                        .content(userMessage)
                        .tokensUsed(usage.getPromptTokens())
                        .build()
        );

        ChatMessage assistantMessage = chatMessageRepository.save(
                ChatMessage.builder()
                        .chatSession(chatSession)
                        .role(MessageRole.ASSISTANT)
                        .content("Assistant message here....")
                        .tokensUsed(usage.getCompletionTokens())
                        .build()
        );
        List<ChatEvent> chatEvents = llmResponseParser.parseChatEvents(fullText, assistantMessage);

        chatEvents.add(ChatEvent.builder()
                .chatMessage(assistantMessage)
                .type(ChatEventType.THOUGHT)
                .sequenceOrder(0)
                .content("Thought for " + duration + "s")
                .build());
        chatEventRepository.saveAll(chatEvents);
        chatEvents.
                stream()
                .filter(chatEvent ->  chatEvent.getType() == ChatEventType.FILE_EDIT)
                .forEach(chatEvent -> projectFileService.saveFile(
                        projectId,
                        chatEvent.getFilePath(),
                        chatEvent.getContent()
                ));


    }
}
