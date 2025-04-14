package com.hanyahunya.gitRemind.config;

import com.hanyahunya.gitRemind.repository.MemberRepository;
import com.hanyahunya.gitRemind.repository.MemberRepositoryImpl;
import com.hanyahunya.gitRemind.service.MemberService;
import com.hanyahunya.gitRemind.service.MemberServiceImpl;
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
        return new MemberServiceImpl(memberRepository());
    }

    @Bean
    public MemberRepository memberRepository() {
        return new MemberRepositoryImpl(dataSource);
    }
}
