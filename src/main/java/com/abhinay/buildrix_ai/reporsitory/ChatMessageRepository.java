package com.abhinay.buildrix_ai.reporsitory;

import com.abhinay.buildrix_ai.entity.ChatMessage;
import com.abhinay.buildrix_ai.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {

    @Query("""
        SELECT DISTINCT cm FROM ChatMessage cm
        LEFT JOIN FETCH cm.events e
        WHERE cm.chatSession = :chatSession
        ORDER BY cm.createdAt ASC, e.sequenceOrder ASC""")
    List<ChatMessage> findChatMessagesByChatSession(@Param("chatSession") ChatSession chatSession);
}
