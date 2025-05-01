package com.hanyahunya.gitRemind.token.service;

import io.jsonwebtoken.Claims;

import java.util.Date;

public interface AccessTokenService {
    String generateToken(String memberId, String tokenId);

    boolean validateToken(String token);

    Claims getClaims(String token);

    boolean isTokenExpired(Date expirationDate);
}
