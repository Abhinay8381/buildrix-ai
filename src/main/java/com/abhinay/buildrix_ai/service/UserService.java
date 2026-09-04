package com.abhinay.buildrix_ai.service;

import com.abhinay.buildrix_ai.dto.auth.UserProfileResponse;
import com.abhinay.buildrix_ai.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    UserProfileResponse getUserProfile(UUID id);

    Optional<User> getUserById(UUID uuid);
}
