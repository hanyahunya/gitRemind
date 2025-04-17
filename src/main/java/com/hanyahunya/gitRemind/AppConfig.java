package com.hanyahunya.gitRemind;

import com.hanyahunya.gitRemind.infrastructure.email.SendEmailService;
import com.hanyahunya.gitRemind.infrastructure.email.SendEmailServiceImpl;
import com.hanyahunya.gitRemind.member.repository.MemberRepository;
import com.hanyahunya.gitRemind.member.repository.MemberRepositoryImpl;
import com.hanyahunya.gitRemind.member.service.*;
import com.hanyahunya.gitRemind.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;

import javax.sql.DataSource;

@Configuration
public class AppConfig {
    private final DataSource dataSource;
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Autowired
    public AppConfig(DataSource dataSource, JavaMailSender javaMailSender, TemplateEngine templateEngine) {this.dataSource = dataSource;
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

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

    @Bean
    public SendEmailService sendEmailService() {
        return new SendEmailServiceImpl(javaMailSender, templateEngine);
    }

    @Bean
    public AuthCodeService authCodeService() {
        return new AuthCodeServiceImpl(sendEmailService());
    }
}
