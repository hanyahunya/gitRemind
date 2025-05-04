package com.hanyahunya.gitRemind.token.service;

import io.jsonwebtoken.Claims;

public interface EmailValidateTokenService {
    String generateToken(String email, TokenPurpose purpose);

    boolean validateToken(String token);

    Claims getClaims(String token);
}
