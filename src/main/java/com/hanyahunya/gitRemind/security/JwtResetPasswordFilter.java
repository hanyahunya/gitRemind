package com.hanyahunya.gitRemind.security;

import com.hanyahunya.gitRemind.token.service.PwTokenService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class JwtResetPasswordFilter extends OncePerRequestFilter {
    private final StringRedisTemplate redisTemplate;
    private final PwTokenService pwTokenService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();

        if (!uri.startsWith("/member/reset-password")) {
            filterChain.doFilter(request, response);
            return;
        }

        String resetPasswordToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("reset_password_token")) {
                    resetPasswordToken = cookie.getValue();
                }
            }
        }
        if (resetPasswordToken == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        Claims claims;
        try {
            claims = pwTokenService.getClaims(resetPasswordToken);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String prefix = "blacklist:";
        if (!redisTemplate.hasKey(prefix + resetPasswordToken) || pwTokenService.validateToken(resetPasswordToken)) {
            String email = claims.get("email", String.class);
            if (email != null) {
                Boolean added = redisTemplate.opsForValue().setIfAbsent(prefix + resetPasswordToken, "blacklisted", Duration.ofMinutes(5));
                if (Boolean.TRUE.equals(added)) {
                    UserPrincipal userPrincipal = new UserPrincipal(null, null, email);
                    Authentication auth = new UsernamePasswordAuthenticationToken(userPrincipal, null, null);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
