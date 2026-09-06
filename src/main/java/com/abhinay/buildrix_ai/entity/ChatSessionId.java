package com.abhinay.buildrix_ai.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ChatSessionId {

    private String userId;
    private String projectId;
}
