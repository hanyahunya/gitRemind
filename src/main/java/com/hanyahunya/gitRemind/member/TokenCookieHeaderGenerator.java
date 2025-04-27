package com.hanyahunya.gitRemind.member;

import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;

public class TokenCookieHeaderGenerator {

    @Value("${jwt.accessToken.expiration}")
    private long accessTokenExpirationTime;
    @Value("${jwt.refreshToken.expiration}")
    private long refreshTokenExpirationTime;

    public String buildByAccessToken(String accessToken) {
        Cookie cookie = new Cookie("accessToken", accessToken);
        cookie.setHttpOnly(true); // JSでアクセス不可
//        cookie.setSecure(true); // HTTPS通信のみ送る
        cookie.setPath("/");
        cookie.setMaxAge((int) (accessTokenExpirationTime / 1000));

        return String.format(
                "%s=%s; Max-Age=%d; Path=%s; HttpOnly; Secure; SameSite=Lax",
                cookie.getName(),
                cookie.getValue(),
                cookie.getMaxAge(),
                cookie.getPath()
        );
    }

    public String buildByRefreshToken(String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
//        cookie.setSecure(true); // HTTPS通信のみ送る
        cookie.setPath("/");
        cookie.setMaxAge((int) (refreshTokenExpirationTime / 1000));

        return String.format(
                "%s=%s; Max-Age=%d; Path=%s; HttpOnly; Secure; SameSite=Lax",
                cookie.getName(),
                cookie.getValue(),
                cookie.getMaxAge(),
                cookie.getPath()
        );
    }

}
