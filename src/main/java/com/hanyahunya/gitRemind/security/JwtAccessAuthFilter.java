    package com.hanyahunya.gitRemind.security;

    import com.hanyahunya.gitRemind.token.service.AccessTokenService;
    import com.hanyahunya.gitRemind.util.cookieHeader.TokenCookieHeaderGenerator;
    import io.jsonwebtoken.Claims;
    import io.jsonwebtoken.ExpiredJwtException;
    import jakarta.servlet.FilterChain;
    import jakarta.servlet.ServletException;
    import jakarta.servlet.http.Cookie;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.HttpHeaders;
    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.stereotype.Component;
    import org.springframework.web.filter.OncePerRequestFilter;

    import java.io.IOException;

    @Component
    @RequiredArgsConstructor
    public class JwtAccessAuthFilter extends OncePerRequestFilter {
        private final AccessTokenService accessTokenService;
        private final TokenCookieHeaderGenerator tokenCookieHeaderGenerator;

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            String uri = request.getRequestURI();

            if (uri.startsWith("/member/reset-password") || uri.startsWith("/member/join")) {
                filterChain.doFilter(request, response);
                return;
            }
            String accessToken = null;
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("access_token")) {
                        accessToken = cookie.getValue();
                    }
                }
            }
            if (accessToken == null) {
                filterChain.doFilter(request, response);
                return;
            }

            Claims claims;
            try {
                claims = accessTokenService.getClaims(accessToken);
            } catch (ExpiredJwtException e) {
                filterChain.doFilter(request, response);
                return;
            } catch (RuntimeException e) {
                String deleteAccessToken = tokenCookieHeaderGenerator.deleteAccessToken();
                String deleteRefreshToken = tokenCookieHeaderGenerator.deleteRefreshToken();
                response.addHeader(HttpHeaders.SET_COOKIE, deleteAccessToken);
                response.addHeader(HttpHeaders.SET_COOKIE, deleteRefreshToken);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            String memberId = claims.get("member_id", String.class);
            String tokenId = claims.get("token_id", String.class);
            if (memberId != null) {
                // principalにmid登録
                UserPrincipal userPrincipal = new UserPrincipal(tokenId, memberId, null);
                // spring securityでの確認オブジェクトAuthenticationを作る　new Username~~~(ユーザーの情報、password、権限リスト(Authorities）)
                Authentication auth = new UsernamePasswordAuthenticationToken(userPrincipal, null, null);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

            filterChain.doFilter(request, response);
        }
    }