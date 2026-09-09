package com.abhinay.buildrix_ai.mapper;

import com.abhinay.buildrix_ai.dto.chat.ChatResponse;
import com.abhinay.buildrix_ai.entity.ChatMessage;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ChatMapper {

    List<ChatResponse> fromListOfChatMessage(List<ChatMessage> chatMessageList);
}
