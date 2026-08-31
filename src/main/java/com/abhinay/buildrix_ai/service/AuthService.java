package com.abhinay.buildrix_ai.service;

import com.abhinay.buildrix_ai.dto.auth.AuthResponse;
import com.abhinay.buildrix_ai.dto.auth.LoginRequest;
import com.abhinay.buildrix_ai.dto.auth.SignUpRequest;

public interface AuthService {
    AuthResponse login(LoginRequest request);

    AuthResponse signup(SignUpRequest request);
}
