package com.hanyahunya.gitRemind.member.service.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtPwTokenService implements PwTokenService {
    @Value("${GIT_REMIND_JWT_KEY}")
    private String jwtKey;

    private SecretKey key;

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(jwtKey.getBytes(StandardCharsets.UTF_8));
    }

    private static final long EXPIRATION_TIME = 1000 * 60 * 5;

    @Override
    public String generateToken(String email) {
        return Jwts.builder()
                .claim("purpose", "password_reset")
                .claim("email", email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    @Override
    public boolean validateToken(String token) {
        return !isTokenExpired(getClaims(token).getExpiration());
    }

    @Override
    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isTokenExpired(Date expirationDate) {
        return expirationDate.before(new Date());
    }
}
