package com.abhinay.buildrix_ai.reporsitory;

import com.abhinay.buildrix_ai.entity.ChatEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChatEventRepository extends JpaRepository<ChatEvent, UUID> {
}
