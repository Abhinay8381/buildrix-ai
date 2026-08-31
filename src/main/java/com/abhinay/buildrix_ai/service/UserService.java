package com.abhinay.buildrix_ai.service;

import com.abhinay.buildrix_ai.dto.auth.UserProfileResponse;

import java.util.UUID;

public interface UserService {
    UserProfileResponse getUserProfile(UUID id);
}
