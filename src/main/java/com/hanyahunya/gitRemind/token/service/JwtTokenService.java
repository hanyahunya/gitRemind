package com.hanyahunya.gitRemind.token.service;

import com.hanyahunya.gitRemind.member.entity.Member;
import com.hanyahunya.gitRemind.member.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@RequiredArgsConstructor
public class JwtTokenService implements AccessTokenService {
    private final MemberRepository memberRepository;


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
                .claim("purpose", "access")
                .claim("member_id", member.getMemberId())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    @Override
    public boolean validateToken(String token) {
        // Tokenの有効期限とTokenVersionの一致を確認
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
