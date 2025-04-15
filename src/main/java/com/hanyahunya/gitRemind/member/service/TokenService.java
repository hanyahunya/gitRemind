package com.hanyahunya.gitRemind.member.service;

import com.hanyahunya.gitRemind.entity.Member;
import io.jsonwebtoken.Claims;

public interface TokenService {
    String generateToken(Member member);

    boolean validateToken(String token);

    Claims getClaims(String token);
}
