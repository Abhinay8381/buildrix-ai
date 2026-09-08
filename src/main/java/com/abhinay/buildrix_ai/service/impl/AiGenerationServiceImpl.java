package com.abhinay.buildrix_ai.service.impl;

import com.abhinay.buildrix_ai.llm.PromptUtils;
import com.abhinay.buildrix_ai.llm.advisors.FileTreeAdvisor;
import com.abhinay.buildrix_ai.security.AuthUtil;
import com.abhinay.buildrix_ai.service.AiGenerationService;
import com.abhinay.buildrix_ai.service.ProjectFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@Slf4j
@RequiredArgsConstructor
public class AiGenerationServiceImpl implements AiGenerationService {

    private final ChatClient chatClient;
    private final AuthUtil authUtil;
    private final ProjectFileService projectFileService;
    private final FileTreeAdvisor fileTreeAdvisor;

    private static final Pattern FILE_TAG_PATTERN = Pattern.compile("<file path = \"([^\"]+)\">(.*?)</file>", Pattern.DOTALL);

    @PreAuthorize("@security.canEditProject(#projectId)")
    @Override
    public Flux<String> streamResponse(String message, UUID projectId) {

        UUID userId = authUtil.getCurrentUserId();
        
        createChatSessionIfNotExists(projectId, userId);
        Map<String, Object> advisorParam = Map.of(
                "user_id", userId,
                "project_id", projectId
        );

        StringBuilder bufferedResponse = new StringBuilder();
       return chatClient.
                prompt()
               .system(PromptUtils.CODE_GENERATION_SYSTEM_PROMPT)
               .user(message)
                .advisors(
                        advisorSpec -> {
                            advisorSpec.params(advisorParam);
                            advisorSpec.advisors(fileTreeAdvisor);
                        }
                ).stream()
                .chatResponse()
               .doOnNext(chatResponse -> {
                   bufferedResponse.append(Objects.requireNonNull(
                           chatResponse.getResult()).getOutput().getText());
               })
               .doOnComplete(() -> {
                           Schedulers.boundedElastic().schedule(() ->
                                   parseAndSaveFiles(projectId, bufferedResponse.toString()));
                             }
                       )
                .doOnError(error ->{
                    log.warn("Error during chat stream from LLM for projectId: {}", projectId, error);
                })
               .map(chatResponse -> Objects.requireNonNull(
                       Objects.requireNonNull(chatResponse.getResult()).getOutput().getText()));

    }

    private void parseAndSaveFiles(UUID projectId, String response) {
//        String dummy = """
//                <message>The LLM message</message>
//
//                <file path = "/src/App.jsx">
//                import ......
//                ..... actual file code
//                </file>
//                """;
        Matcher matcher = FILE_TAG_PATTERN.matcher(response);
        while (matcher.find()){
            String filePath = matcher.group(1);
            String content = matcher.group(2).trim();

            projectFileService.saveFile(projectId, filePath, content);
        }
    }

    private void createChatSessionIfNotExists(UUID projectId, UUID userId) {
    }
}
