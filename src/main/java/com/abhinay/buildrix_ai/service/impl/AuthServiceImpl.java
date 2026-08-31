package com.abhinay.buildrix_ai.service.impl;

import com.abhinay.buildrix_ai.dto.auth.AuthResponse;
import com.abhinay.buildrix_ai.dto.auth.LoginRequest;
import com.abhinay.buildrix_ai.dto.auth.SignUpRequest;
import com.abhinay.buildrix_ai.service.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    @Override
    public AuthResponse login(LoginRequest request) {
        return null;
    }

    @Override
    public AuthResponse signup(SignUpRequest request) {
        return null;
    }
}
