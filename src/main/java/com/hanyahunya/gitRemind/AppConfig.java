package com.hanyahunya.gitRemind;

import com.hanyahunya.gitRemind.member.repository.MemberRepository;
import com.hanyahunya.gitRemind.member.repository.MemberRepositoryImpl;
import com.hanyahunya.gitRemind.member.service.JwtTokenService;
import com.hanyahunya.gitRemind.member.service.MemberService;
import com.hanyahunya.gitRemind.member.service.MemberServiceImpl;
import com.hanyahunya.gitRemind.member.service.TokenService;
import com.hanyahunya.gitRemind.security.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class AppConfig {
    private final DataSource dataSource;

    @Autowired
    public AppConfig(DataSource dataSource) {this.dataSource = dataSource;}

    @Bean
    public MemberService memberService() {
        return new MemberServiceImpl(memberRepository(), tokenService());
    }

    @Bean
    public MemberRepository memberRepository() {
        return new MemberRepositoryImpl(dataSource);
    }

    @Bean
    public TokenService tokenService() {
        return new JwtTokenService();
    }
}
