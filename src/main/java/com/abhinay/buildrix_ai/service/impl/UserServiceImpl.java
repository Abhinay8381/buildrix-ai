package com.abhinay.buildrix_ai.service.impl;

import com.abhinay.buildrix_ai.dto.auth.UserProfileResponse;
import com.abhinay.buildrix_ai.service.UserService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public UserProfileResponse getUserProfile(UUID id) {
        return null;
    }
}
