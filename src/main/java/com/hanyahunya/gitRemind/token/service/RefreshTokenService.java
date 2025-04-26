package com.hanyahunya.gitRemind.token.service;

import io.jsonwebtoken.Claims;

public interface RefreshTokenService {
    String generateToken(String mid);

    boolean validateToken(String token);

    Claims getClaims(String token);
}
