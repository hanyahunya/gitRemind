package com.hanyahunya.gitRemind.token.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtRefreshTokenService implements RefreshTokenService {

    @Setter
    @Value("${jwt.refreshToken.secret}")
    private String jwtKey;

    private SecretKey key;

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(jwtKey.getBytes(StandardCharsets.UTF_8));
    }

    @Setter // このクラスのインスタンスを作らない限り使えないため、大丈夫そう..
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
        } catch (JwtException e) {
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

    // for test
    public Date getExpirationDate(String token) {
        try {
            Claims claims = getClaims(token);
            return claims.getExpiration();
        } catch (ExpiredJwtException e) {
            return e.getClaims().getExpiration();
        }
    }
//    private boolean isTokenExpired(Date expirationDate) {
//        return expirationDate.before(new Date());
//    }
}
