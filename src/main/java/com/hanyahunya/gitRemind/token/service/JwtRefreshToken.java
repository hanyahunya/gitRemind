package com.hanyahunya.gitRemind.token.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

public class JwtRefreshToken implements RefreshTokenService {

    @Value("${GIT_REMIND_JWT_KEY}")
    private String jwtKey;

    private SecretKey key;

//    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(jwtKey.getBytes(StandardCharsets.UTF_8));
    }

    // 保存期間（7日）
    private static final long EXPIRATION_TIME = 60 * 60 * 24 * 7;

    @Override
    public String generateToken(String mid) {

        return "";
    }

    @Override
    public boolean validateToken(String token) {
        return false;
    }

    @Override
    public Claims getClaims(String token) {
        return null;
    }
}
