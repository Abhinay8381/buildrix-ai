package com.abhinay.buildrix_ai.security;

import com.abhinay.buildrix_ai.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
public class AuthUtil {

    @Value("${jwt.secret-key}")
    private String jwtKey;

    private SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(jwtKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(User user){
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
                .signWith(getSecretKey())
                .compact();
    }

    public JwtUserPrincipal verifyToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSecretKey())
                .build().parseSignedClaims(token)
                .getPayload();
        return new JwtUserPrincipal(claims.getSubject(), UUID.fromString(claims.get("userId", String.class)));
    }

    public UUID getCurrentUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !(authentication.getPrincipal() instanceof JwtUserPrincipal))
            throw new AuthenticationCredentialsNotFoundException("No JWT Found");
        return ((JwtUserPrincipal) authentication.getPrincipal()).userId();
    }
}
