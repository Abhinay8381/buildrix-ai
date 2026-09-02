package com.abhinay.buildrix_ai.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private final AuthUtil authUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("Incoming request at JWT Auth filter :{}", request.getRequestURI());
        String authHeader = request.getHeader(AUTH_HEADER);
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            String token = authHeader.substring(BEARER_PREFIX.length());

            JwtUserPrincipal jwtUserPrincipal = authUtil.verifyToken(token);
            if(jwtUserPrincipal != null && SecurityContextHolder.getContext().getAuthentication() == null){
                Authentication authentication = new UsernamePasswordAuthenticationToken(jwtUserPrincipal, null,
                        new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("JWT Auth filter set authentication for user: {}", jwtUserPrincipal.email());
            }
        }
        filterChain.doFilter(request, response);
    }
}
