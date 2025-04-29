    package com.hanyahunya.gitRemind.security;

    import com.hanyahunya.gitRemind.token.service.PwTokenService;
    import com.hanyahunya.gitRemind.token.service.AccessTokenService;
    import com.hanyahunya.gitRemind.token.service.RefreshTokenService;
    import com.hanyahunya.gitRemind.token.service.TokenService;
    import com.hanyahunya.gitRemind.util.cookieHeader.TokenCookieHeaderGenerator;
    import io.jsonwebtoken.Claims;
    import io.jsonwebtoken.ExpiredJwtException;
    import jakarta.servlet.FilterChain;
    import jakarta.servlet.ServletException;
    import jakarta.servlet.http.Cookie;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
    import lombok.RequiredArgsConstructor;
    import org.springframework.data.redis.core.StringRedisTemplate;
    import org.springframework.http.HttpHeaders;
    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.stereotype.Component;
    import org.springframework.web.filter.OncePerRequestFilter;

    import java.io.IOException;

    @Component
    @RequiredArgsConstructor
    public class JwtAuthFilter extends OncePerRequestFilter {
        private final AccessTokenService accessTokenService;
        private final TokenCookieHeaderGenerator tokenCookieHeaderGenerator;
        private final RefreshTokenService refreshTokenService;
        private final TokenService tokenService;
        private final PwTokenService pwTokenService;
        private final StringRedisTemplate redisTemplate;

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
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
            if (memberId != null) {
                // principalにmid登録
                UserPrincipal userPrincipal = new UserPrincipal(memberId, null);
                // spring securityでの確認オブジェクトAuthenticationを作る　new Username~~~(ユーザーの情報、password、権限リスト(Authorities）)
                Authentication auth = new UsernamePasswordAuthenticationToken(userPrincipal, null, null);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

            filterChain.doFilter(request, response);
        }
    }

//            String authHeader = request.getHeader("Authorization");
//
//            // JwtAuthFilterはspring security認証の前に実行されるため、多重ifで囲む
//            if (authHeader != null && authHeader.startsWith("Bearer ")) {
//                String token = authHeader.substring(7);
//                Claims claims;
//                try {
//                    claims = accessTokenService.getClaims(token);
//                } catch (ExpiredJwtException e) {
//                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                    return;
//                }
//                if((claims.get("purpose", String.class).equals("access"))) {
//                    if (accessTokenService.validateToken(token)) {
//                        String memberId = claims.get("member_id", String.class);
//                        if (memberId != null) {
//                            // principalにmid登録
//                            UserPrincipal userPrincipal = new UserPrincipal(memberId, null);
//                            // spring securityでの確認オブジェクトAuthenticationを作る　new Username~~~(ユーザーの情報、password、権限リスト(Authorities）)
//                            Authentication auth = new UsernamePasswordAuthenticationToken(userPrincipal, null, null);
//                            SecurityContextHolder.getContext().setAuthentication(auth);
//                        }
//                    }
//                } else if ((claims.get("purpose", String.class).equals("password_reset"))) {
//                    String prefix = "blacklist:";
//                    if (!redisTemplate.hasKey(prefix + token) || pwTokenService.validateToken(token)) {
//                        String email = claims.get("email", String.class);
//                        if (email != null) {
//
//                            Boolean added = redisTemplate.opsForValue().setIfAbsent(prefix + token, "blacklisted", Duration.ofMinutes(5));
//                            if (Boolean.TRUE.equals(added)) {
//                                UserPrincipal userPrincipal = new UserPrincipal(null, email);
//                                Authentication auth = new UsernamePasswordAuthenticationToken(userPrincipal, null, null);
//                                SecurityContextHolder.getContext().setAuthentication(auth);
//                            }
//                        }
//                    }
//                }
//
//
//            }
//            filterChain.doFilter(request, response);

