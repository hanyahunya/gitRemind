package com.hanyahunya.gitRemind.member.service.token;

import io.jsonwebtoken.Claims;

public interface PwTokenService {
    String generateToken(String email);

    boolean validateToken(String token);

    Claims getClaims(String token);
}
