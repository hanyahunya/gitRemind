package com.hanyahunya.gitRemind.token.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtPwTokenService implements PwTokenService {
    @Value("${jwt.pwToken.secret}")
    private String jwtKey;

    private SecretKey key;

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(jwtKey.getBytes(StandardCharsets.UTF_8));
    }

    @Value("${jwt.pwToken.expiration}")
    private long expirationTime;

    @Override
    public String generateToken(String email, TokenPurpose purpose) {
        return Jwts.builder()
                .claim("email", email)
                .claim("purpose", purpose.name().toLowerCase())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key)
                .compact();
    }

    @Override
    public boolean validateToken(String token) {
        try{
            getClaims(token);
            return true;
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

//    private boolean isTokenExpired(Date expirationDate) {
//        return expirationDate.before(new Date());
//    }
}
