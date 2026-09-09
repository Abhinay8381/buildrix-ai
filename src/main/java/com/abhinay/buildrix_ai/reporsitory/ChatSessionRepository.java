package com.abhinay.buildrix_ai.reporsitory;

import com.abhinay.buildrix_ai.entity.ChatMessage;
import com.abhinay.buildrix_ai.entity.ChatSession;
import com.abhinay.buildrix_ai.entity.ChatSessionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatSessionRepository extends JpaRepository<ChatSession, ChatSessionId> {

}
