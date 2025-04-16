package com.hanyahunya.gitRemind.security;

import com.hanyahunya.gitRemind.member.service.TokenService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        // JwtAuthFilterはspring security認証の前に実行されるため、多重ifで囲む
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (tokenService.validateToken(token)) {
                Claims claims = tokenService.getClaims(token);
                String mid = claims.get("mid", String.class);
                if (mid != null) {
                    // principalにmid登録
                    UserPrincipal userPrincipal = new UserPrincipal(mid);
                    // spring securityでの確認オブジェクトAuthenticationを作る　new Username~~~(ユーザーの情報、pw、権限リスト(Authorities）)
                    Authentication auth = new UsernamePasswordAuthenticationToken(userPrincipal, null, null);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
