package com.hanyahunya.gitRemind.security;

import com.hanyahunya.gitRemind.token.service.EmailValidateTokenService;
import com.hanyahunya.gitRemind.token.service.TokenPurpose;
import com.hanyahunya.gitRemind.util.cookieHeader.TokenCookieHeaderGenerator;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class EmailValidateAuthFilter extends OncePerRequestFilter {
    private final StringRedisTemplate redisTemplate;
    private final EmailValidateTokenService emailValidateTokenService;
    private final TokenCookieHeaderGenerator tokenCookieHeaderGenerator;

    @Value("${jwt.validateToken.expiration}")
    private long expirationTime;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();

        if (!uri.startsWith("/member/reset-password") && !uri.startsWith("/member/join")) {
            filterChain.doFilter(request, response);
            return;
        }

        String resetPasswordToken = null;
        String emailVerificationToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("reset_password_token")) {
                    resetPasswordToken = cookie.getValue();
                } else if (cookie.getName().equals("email_verification_token")) {
                    emailVerificationToken = cookie.getValue();
                }
            }
        }
        if (resetPasswordToken == null && emailVerificationToken == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        Claims claims = null;

        try {
            if (resetPasswordToken != null && uri.startsWith("/member/reset-password")) {
                claims = emailValidateTokenService.getClaims(resetPasswordToken);
            }
            if (emailVerificationToken != null && uri.startsWith("/member/join")) {
                claims = emailValidateTokenService.getClaims(emailVerificationToken);
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String purpose = claims.get("purpose", String.class);
        String email = claims.get("email", String.class);

        String deleteValidateToken = null;
        if (uri.startsWith("/member/reset-password") && purpose.equals(TokenPurpose.RESET_PASSWORD.name().toLowerCase())) {
            verifyAndAuthenticate(resetPasswordToken, email);
            deleteValidateToken = tokenCookieHeaderGenerator.deleteValidateToken(TokenPurpose.RESET_PASSWORD);
        }
        if (uri.startsWith("/member/join") && purpose.equals(TokenPurpose.EMAIL_VERIFICATION.name().toLowerCase())) {
            verifyAndAuthenticate(emailVerificationToken, email);
            deleteValidateToken = tokenCookieHeaderGenerator.deleteValidateToken(TokenPurpose.EMAIL_VERIFICATION);
        }
        response.addHeader(HttpHeaders.SET_COOKIE, deleteValidateToken);

        filterChain.doFilter(request, response);
    }

    private void verifyAndAuthenticate(String token, String email) {
        String prefix = "blacklist:";
        if (!redisTemplate.hasKey(prefix + token)) {
             Boolean added = redisTemplate.opsForValue().setIfAbsent(prefix + token, "blacklisted", Duration.ofMillis(expirationTime));
             if (Boolean.TRUE.equals(added)) {
                 UserPrincipal userPrincipal = new UserPrincipal(null, null, email);
                 Authentication auth = new UsernamePasswordAuthenticationToken(userPrincipal, null, null);
                 SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
    }


}
