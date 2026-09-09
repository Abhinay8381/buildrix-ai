package com.abhinay.buildrix_ai.service.impl;

import com.abhinay.buildrix_ai.dto.auth.AuthResponse;
import com.abhinay.buildrix_ai.dto.auth.LoginRequest;
import com.abhinay.buildrix_ai.dto.auth.RefreshTokenRequest;
import com.abhinay.buildrix_ai.dto.auth.SignUpRequest;
import com.abhinay.buildrix_ai.entity.User;
import com.abhinay.buildrix_ai.exceptions.BadRequestException;
import com.abhinay.buildrix_ai.exceptions.ResourceNotFoundException;
import com.abhinay.buildrix_ai.mapper.UserMapper;
import com.abhinay.buildrix_ai.reporsitory.UserRepository;
import com.abhinay.buildrix_ai.security.AuthUtil;
import com.abhinay.buildrix_ai.security.JwtUserPrincipal;
import com.abhinay.buildrix_ai.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final AuthUtil authUtil;

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        User user = (User) authentication.getPrincipal();
        return new AuthResponse(
                authUtil.generateAccessToken(user),
                authUtil.generateRefreshToken(user),
                userMapper.toUserProfileResponse(user)
        );
    }

    @Transactional
    @Override
    public AuthResponse signup(SignUpRequest request) {
        userRepository.findByEmail(request.email()).ifPresent(user -> {
            throw new BadRequestException("User with email: " + request.email() + " already exists");
        });
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return new AuthResponse(
                authUtil.generateAccessToken(user),
                authUtil.generateRefreshToken(user),
                userMapper.toUserProfileResponse(user)
        );
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        JwtUserPrincipal principal = authUtil.verifyToken(request.refreshToken());
        User user = userRepository.findById(principal.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User", principal.userId().toString()));

        return new AuthResponse(
                authUtil.generateAccessToken(user),
                authUtil.generateRefreshToken(user),
                userMapper.toUserProfileResponse(user)
        );
    }
}

