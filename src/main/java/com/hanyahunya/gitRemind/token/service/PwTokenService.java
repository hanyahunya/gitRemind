package com.hanyahunya.gitRemind.token.service;

import io.jsonwebtoken.Claims;

public interface PwTokenService {
    String generateToken(String email);

    boolean validateToken(String token);

    Claims getClaims(String token);
}
