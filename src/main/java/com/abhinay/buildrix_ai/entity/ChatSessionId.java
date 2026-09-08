package com.abhinay.buildrix_ai.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.UUID;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ChatSessionId {

    private UUID userId;
    private UUID projectId;
}
