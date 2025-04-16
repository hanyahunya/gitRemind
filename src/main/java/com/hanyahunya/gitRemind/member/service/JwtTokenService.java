package com.hanyahunya.gitRemind.member.service;

import com.hanyahunya.gitRemind.member.entity.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtTokenService implements TokenService {

    @Value("${GIT_REMIND_JWT_KEY}")
    private String jwtKey;

    private SecretKey key;

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(jwtKey.getBytes(StandardCharsets.UTF_8));
    }

    private static final long EXPIRATION_TIME = 60 * 60 * 24 * 7;

    @Override
    public String generateToken(Member member) {
        return Jwts.builder()
                .claim("mid", member.getMid())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            return !isTokenExpired(getClaims(token).getExpiration());
        } catch (Exception e) {
            return false;
        }
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
