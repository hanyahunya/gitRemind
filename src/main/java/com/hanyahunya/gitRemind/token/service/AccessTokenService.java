package com.hanyahunya.gitRemind.token.service;

import com.hanyahunya.gitRemind.member.entity.Member;
import io.jsonwebtoken.Claims;

public interface AccessTokenService {
    String generateToken(Member member);

    boolean validateToken(String token);

    Claims getClaims(String token);
}
