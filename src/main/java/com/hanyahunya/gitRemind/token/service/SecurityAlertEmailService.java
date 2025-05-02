package com.hanyahunya.gitRemind.token.service;

import com.hanyahunya.gitRemind.infrastructure.email.SendEmailService;
import com.hanyahunya.gitRemind.member.entity.Member;
import com.hanyahunya.gitRemind.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class SecurityAlertEmailService {
    private final SendEmailService sendEmailService;
    private final MemberRepository memberRepository;

    public void sendCookieHijackingAlert(String email, String gitUsername) {
        Optional<Member> memberOptional = memberRepository.findMemberByEmail(email);
        if (memberOptional.isEmpty()) {
            return;
        }
        switch (memberOptional.get().getCountry().toLowerCase()) {
            case "kr":
                sendToKorea(email, gitUsername);
                break;
            case "jp":
                sendToJapan(email, gitUsername);
                break;
            default:
                sendToUnitedStates(email, gitUsername);
                break;
        }
    }

    private void sendToKorea(String email, String gitUsername) {
        if (gitUsername == null) {
            gitUsername = "사용자";
        }
        Map<String, Object> params = new HashMap<>();
        params.put("name", gitUsername);
        sendEmailService.sendEmail(email, "보안 알림", "kr/accountAlert", params);
    }

    private void sendToJapan(String email, String gitUsername) {
        if (gitUsername == null) {
            gitUsername = "ユーザー";
        }
        Map<String, Object> params = new HashMap<>();
        params.put("name", gitUsername);
        sendEmailService.sendEmail(email, "セキュリティ通知", "jp/accountAlert", params);
    }

    private void sendToUnitedStates(String email, String gitUsername) {
        if (gitUsername == null) {
            gitUsername = "User";
        }
        Map<String, Object> params = new HashMap<>();
        params.put("name", gitUsername);
        sendEmailService.sendEmail(email, "Verification Code", "us/accountAlert", params);
    }
}
