package com.hanyahunya.gitRemind.unit.token.service;

import com.hanyahunya.gitRemind.infrastructure.email.SendEmailService;
import com.hanyahunya.gitRemind.member.entity.Member;
import com.hanyahunya.gitRemind.member.repository.MemberRepository;
import com.hanyahunya.gitRemind.token.service.SecurityAlertEmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityAlertEmailServiceTest {
    @InjectMocks
    private SecurityAlertEmailService securityAlertEmailService;
    @Mock
    private SendEmailService sendEmailService;
    @Mock
    private MemberRepository memberRepository;

    @Test
    @DisplayName("<sendCookieHijackingAlert> KR")
    void sendCookieHijackingAlert() {
        // given
        String email = "test@example.com";
        String gitUsername = "hanyahunya";

        Member dbMember = Member.builder().email(email).country("KR").build();
        when(memberRepository.findMemberByEmail(email)).thenReturn(Optional.of(dbMember));
        // when
        securityAlertEmailService.sendCookieHijackingAlert(email, gitUsername);
        // then
        verify(sendEmailService).sendEmail(email, "보안 알림", "kr/accountAlert", Map.of("name", gitUsername));
    }
    @Test
    @DisplayName("<sendCookieHijackingAlert> JP")
    void sendCookieHijackingAlertToJP() {
        // given
        String email = "test@example.com";
        String gitUsername = "hanyahunya";

        Member dbMember = Member.builder().email(email).country("JP").build();
        when(memberRepository.findMemberByEmail(email)).thenReturn(Optional.of(dbMember));
        // when
        securityAlertEmailService.sendCookieHijackingAlert(email, gitUsername);
        // then
        verify(sendEmailService).sendEmail(email, "セキュリティ通知", "jp/accountAlert", Map.of("name", gitUsername));
    }
    @Test
    @DisplayName("<sendCookieHijackingAlert> other")
    void sendCookieHijackingAlertToOther() {
        // given
        String email = "test@example.com";
        String gitUsername = "hanyahunya";

        Member dbMember = Member.builder().email(email).country("US").build();
        when(memberRepository.findMemberByEmail(email)).thenReturn(Optional.of(dbMember));
        // when
        securityAlertEmailService.sendCookieHijackingAlert(email, gitUsername);
        // then
        verify(sendEmailService).sendEmail(email, "Verification Code", "us/accountAlert", Map.of("name", gitUsername));
    }
}