package com.abhinay.buildrix_ai.service.impl;

import com.abhinay.buildrix_ai.dto.auth.UserProfileResponse;
import com.abhinay.buildrix_ai.exceptions.ResourceNotFoundException;
import com.abhinay.buildrix_ai.reporsitory.UserRepository;
import com.abhinay.buildrix_ai.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    @Override
    public UserProfileResponse getUserProfile(UUID id) {

        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", email));
    }
}
