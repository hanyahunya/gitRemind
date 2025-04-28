package com.hanyahunya.gitRemind.util;

import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;

public class TokenCookieHeaderGenerator {

//    @Value("${jwt.accessToken.expiration}")
//    private long accessTokenExpirationTime;
    @Value("${jwt.refreshToken.expiration}")
    private long refreshTokenExpirationTime;

    public String buildByAccessToken(String accessToken) {
        Cookie cookie = new Cookie("access_token", accessToken);
        cookie.setHttpOnly(true); // JSでアクセス不可
//        cookie.setSecure(true); // HTTPS通信のみ送る
        cookie.setPath("/");
        cookie.setMaxAge((int) (refreshTokenExpirationTime / 1000));

        return buildCookieHeader(cookie);
    }

    public String buildByRefreshToken(String refreshToken) {
        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true);
//        cookie.setSecure(true); // HTTPS通信のみ送る
        cookie.setPath("/");
        cookie.setMaxAge((int) (refreshTokenExpirationTime / 1000));

        return buildCookieHeader(cookie);
    }

    public String deleteAccessToken() {
        Cookie cookie = new Cookie("access_token", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        return buildCookieHeader(cookie);
    }

    public String deleteRefreshToken() {
        Cookie cookie = new Cookie("refresh_token", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        return buildCookieHeader(cookie);
    }

    private String buildCookieHeader(Cookie cookie) {
        StringBuilder header = new StringBuilder();

        header.append(String.format(
                "%s=%s; Max-Age=%d; Path=%s; SameSite=Lax",
                cookie.getName(),
                cookie.getValue(),
                cookie.getMaxAge(),
                cookie.getPath()
        ));

        if (cookie.isHttpOnly()) {
            header.append("; HttpOnly");
        }
        if (cookie.getSecure()) {
            header.append("; Secure");
        }

        return header.toString();
    }

}
