package com.hanyahunya.gitRemind.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAccessAuthFilter jwtAccessAuthFilter;
    private final EmailValidateAuthFilter emailValidateAuthFilter;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("http://localhost/");// 許可されたUrl
        configuration.addAllowedMethod("*"); // 許可されたメソッド
        configuration.addAllowedHeader("*"); // 許可されたヘッダー
        configuration.setAllowCredentials(true); // クッキーと認証情報を許可

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // すべてのリクエストに CORS 設定適用
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        final String[] whitelist = {
                "/member/login",
                "/auth-code", "/auth-code/validate", "/auth-code/password-code", "/auth-code/validate/password-code",
                "/refreshAccessToken"
        };

        http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .requestMatchers(whitelist).permitAll() // 認証なしにアクセス可能
                                .anyRequest().authenticated() // 他のリクエストは認証必要
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS設定適用
                .csrf(AbstractHttpConfigurer::disable) // CSRF保護未使用
                .addFilterBefore(jwtAccessAuthFilter, UsernamePasswordAuthenticationFilter.class) // JWT追加
                .addFilterBefore(emailValidateAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
