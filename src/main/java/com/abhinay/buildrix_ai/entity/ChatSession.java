package com.abhinay.buildrix_ai.entity;

import lombok.*;

import java.time.Instant;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatSession extends BaseEntity{

    private Project project;
    private User user;
    private String title;
    private Instant deletedAt;
}
