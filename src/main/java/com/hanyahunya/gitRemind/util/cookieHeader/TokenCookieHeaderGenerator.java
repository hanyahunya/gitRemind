package com.hanyahunya.gitRemind.util.cookieHeader;

import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;

public class TokenCookieHeaderGenerator {

//    @Value("${jwt.accessToken.expiration}")
//    private long accessTokenExpirationTime;
    @Value("${jwt.refreshToken.expiration}")
    private long refreshTokenExpirationTime;

    public HttpHeaders handleTokenHeader(SetResultDto requestDto) {
        HttpHeaders headers = new HttpHeaders();
        if (requestDto.getAccessToken() != null) {
//            System.out.println(requestDto.getAccessToken());
            String deleteAccessToken = deleteAccessToken();
            headers.add(HttpHeaders.SET_COOKIE, deleteAccessToken);
            String accessToken = buildByAccessToken(requestDto.getAccessToken());
            headers.add(HttpHeaders.SET_COOKIE, accessToken);
        }
        if (requestDto.getRefreshToken() != null) {
//            System.out.println(requestDto.getRefreshToken());
            String deleteRefreshToken = deleteRefreshToken();
            headers.add(HttpHeaders.SET_COOKIE, deleteRefreshToken);
            String refreshToken = buildByRefreshToken(requestDto.getRefreshToken());
            headers.add(HttpHeaders.SET_COOKIE, refreshToken);
        }
        if (requestDto.isDeleteAccessToken()) {
//            System.out.println("isDeleteAccessToken");
            String deleteAccessToken = deleteAccessToken();
            headers.add(HttpHeaders.SET_COOKIE, deleteAccessToken);
        }
        if (requestDto.isDeleteRefreshToken()) {
//            System.out.println("isDeleteRefreshToken");
            String deleteRefreshToken = deleteRefreshToken();
            headers.add(HttpHeaders.SET_COOKIE, deleteRefreshToken);
        }
        return headers;
    }

    private String buildByAccessToken(String accessToken) {
        Cookie cookie = new Cookie("access_token", accessToken);
        cookie.setHttpOnly(true); // JSでアクセス不可
//        cookie.setSecure(true); // HTTPS通信のみ送る
        cookie.setPath("/");
        cookie.setMaxAge((int) (refreshTokenExpirationTime / 1000));

        return buildCookieHeader(cookie);
    }

    private String buildByRefreshToken(String refreshToken) {
        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true);
//        cookie.setSecure(true); // HTTPS通信のみ送る
        cookie.setPath("/");
        cookie.setMaxAge((int) (refreshTokenExpirationTime / 1000));

        return buildCookieHeader(cookie);
    }

    // !!! public for AuthFilter !!!
    public String deleteAccessToken() {
        Cookie cookie = new Cookie("access_token", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        return buildCookieHeader(cookie);
    }

    // !!! public for AuthFilter !!!
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
