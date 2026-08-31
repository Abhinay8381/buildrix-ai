package com.abhinay.buildrix_ai.entity;

import com.abhinay.buildrix_ai.enums.MessageRole;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage extends BaseEntity{

    private ChatSession chatSession;
    private MessageRole role;
    private String content;
    private String toolCalls;
    private Integer tokensUsed;
}
