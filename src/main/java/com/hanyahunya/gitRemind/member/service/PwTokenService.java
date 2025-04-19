package com.hanyahunya.gitRemind.member.service;

import com.hanyahunya.gitRemind.member.entity.Member;
import io.jsonwebtoken.Claims;

public interface PwTokenService {
    String generateToken(String email);

    boolean validateToken(String token);

    Claims getClaims(String token);
}
