package com.hanyahunya.gitRemind.member.service;

import com.hanyahunya.gitRemind.member.dto.MemberInfoResponseDto;
import com.hanyahunya.gitRemind.member.entity.Member;
import com.hanyahunya.gitRemind.member.repository.MemberRepository;
import com.hanyahunya.gitRemind.util.ResponseDto;
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
public class JwtTokenService implements TokenService {
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
        Member member1 = getMemberByMid(member.getMid());
        return Jwts.builder()
                .claim("mid", member.getMid())
                .claim("token_version", member1.getToken_version())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    @Override
    public boolean validateToken(String token) {
        Claims claims = getClaims(token);
        String mid = claims.get("mid", String.class);
        Integer tokenVersion = claims.get("token_version", Integer.class);
        Member member = getMemberByMid(mid);

        // Tokenの有効期限とTokenVersionの一致を確認
        return (!isTokenExpired(claims.getExpiration()) && tokenVersion.equals(member.getToken_version()));
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
    private Member getMemberByMid(String mid) {
        return memberRepository.findMemberByMid(mid).orElse(null);
    }
}
