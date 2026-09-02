package com.abhinay.buildrix_ai.mapper;

import com.abhinay.buildrix_ai.dto.auth.SignUpRequest;
import com.abhinay.buildrix_ai.dto.auth.UserProfileResponse;
import com.abhinay.buildrix_ai.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserProfileResponse toUserProfileResponse(User user);
    User toEntity(SignUpRequest request);
}
