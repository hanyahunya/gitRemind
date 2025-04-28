package com.hanyahunya.gitRemind.token.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtRefreshTokenService implements RefreshTokenService {

    @Value("${jwt.refreshToken.secret}")
    private String jwtKey;

    private SecretKey key;

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(jwtKey.getBytes(StandardCharsets.UTF_8));
    }

    @Value("${jwt.refreshToken.expiration}")
    private long expirationTime;

    @Override
    public String generateToken(String tokenId) {
        return Jwts.builder()
                .claim("token_id", tokenId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key)
                .compact();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    @Override
    public Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token有効期限切れ");
        } catch (SignatureException e) {
            throw new RuntimeException("Tokenキー認証失敗");
        } catch (JwtException e) {
            throw new RuntimeException("Token認証失敗");
        }
    }

//    private boolean isTokenExpired(Date expirationDate) {
//        return expirationDate.before(new Date());
//    }
}
