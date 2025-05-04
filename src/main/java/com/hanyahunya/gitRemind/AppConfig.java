package com.hanyahunya.gitRemind;

import com.hanyahunya.gitRemind.contribution.repository.ContributionRepository;
import com.hanyahunya.gitRemind.contribution.repository.ContributionRepositoryImpl;
import com.hanyahunya.gitRemind.contribution.service.CommitReminderEmailService;
import com.hanyahunya.gitRemind.contribution.service.ContributionService;
import com.hanyahunya.gitRemind.contribution.service.ContributionServiceImpl;
import com.hanyahunya.gitRemind.contribution.service.SchedulerService;
import com.hanyahunya.gitRemind.infrastructure.email.SendEmailService;
import com.hanyahunya.gitRemind.infrastructure.email.SendEmailServiceImpl;
import com.hanyahunya.gitRemind.infrastructure.github.GithubHtmlScraper;
import com.hanyahunya.gitRemind.util.cookieHeader.TokenCookieHeaderGenerator;
import com.hanyahunya.gitRemind.member.repository.MemberRepository;
import com.hanyahunya.gitRemind.member.repository.MemberRepositoryImpl;
import com.hanyahunya.gitRemind.member.service.*;
import com.hanyahunya.gitRemind.token.repository.MemberTokenRepository;
import com.hanyahunya.gitRemind.token.repository.MemberTokenRepositoryImpl;
import com.hanyahunya.gitRemind.token.repository.TokenRepository;
import com.hanyahunya.gitRemind.token.repository.TokenRepositoryImpl;
import com.hanyahunya.gitRemind.token.service.*;
import com.hanyahunya.gitRemind.util.service.EncodeService;
import com.hanyahunya.gitRemind.util.service.PBKDF2EncodeService;
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
    // ここからutilパッケージ
    @Bean
    public EncodeService encodeService() {
        return new PBKDF2EncodeService();
    }
    // ここまでutilパッケージ

    // ここからmemberパッケージ
    @Bean
    public TokenCookieHeaderGenerator tokenCookieHeaderGenerator() {
        return new TokenCookieHeaderGenerator();
    }
    @Bean
    public MemberService memberService() {
        return new MemberServiceImpl(memberRepository(), tokenService(), encodeService());
    }
    @Bean
    public PasswordService passwordService() {
        return new PasswordServiceImpl(memberRepository(), encodeService(), tokenService());
    }
    @Bean
    public MemberRepository memberRepository() {
        return new MemberRepositoryImpl(dataSource);
    }
    @Bean
    public AuthCodeService authCodeService() {
        return new AuthCodeServiceImpl(sendEmailService(), pwTokenService());
    }
    // ここまでmemberパッケージ

    // ここからinfrastructureパッケージ
    @Bean
    public SendEmailService sendEmailService() {
        return new SendEmailServiceImpl(javaMailSender, templateEngine);
    }
    @Bean
    public GithubHtmlScraper githubHtmlScraper() {
        return new GithubHtmlScraper();
    }
    // ここまでinfrastructureパッケージ

    // ここからtokenパッケージ
    @Bean
    public TokenService tokenService() {
        return new TokenServiceImpl(tokenRepository(), memberTokenRepository(), contributionRepository(),
                accessTokenService(), refreshTokenService(), encodeService(), securityAlertEmailService());
    }
    @Bean
    public AccessTokenService accessTokenService() {
        return new JwtAccessTokenService();
    }
    @Bean
    RefreshTokenService refreshTokenService() {
        return new JwtRefreshTokenService();
    }
    @Bean
    public EmailValidateTokenService pwTokenService() {
        return new JwtEmailValidateTokenService();
    }
    @Bean
    public SecurityAlertEmailService securityAlertEmailService() {
        return new SecurityAlertEmailService(sendEmailService(), memberRepository());
    }
    @Bean
    public TokenRepository tokenRepository() {
        return new TokenRepositoryImpl(dataSource);
    }
    @Bean
    public MemberTokenRepository memberTokenRepository() {
        return new MemberTokenRepositoryImpl(dataSource);
    }
    // ここまでtokenパッケージ

    // ここからcontributionパッケージ
    @Bean
    public ContributionRepository contributionRepository() {
        return new ContributionRepositoryImpl(dataSource);
    }
    @Bean
    public ContributionService contributionService() {
        return new ContributionServiceImpl(contributionRepository());
    }
    @Bean
    public SchedulerService schedulerService() {
        return new SchedulerService(contributionRepository(), githubHtmlScraper(), commitReminderEmailService());
    }
    @Bean
    public CommitReminderEmailService commitReminderEmailService() {
        return new CommitReminderEmailService(sendEmailService(), memberRepository());
    }
    // ここまでcontributionパッケージ
}
