package com.hanyahunya.gitRemind.member.service;

import com.hanyahunya.gitRemind.token.service.EmailValidateTokenService;
import com.hanyahunya.gitRemind.token.service.TokenPurpose;
import com.hanyahunya.gitRemind.util.ResponseDto;
import com.hanyahunya.gitRemind.infrastructure.email.SendEmailService;
import com.hanyahunya.gitRemind.member.dto.EmailRequestDto;
import com.hanyahunya.gitRemind.member.dto.ValidateCodeRequestDto;
import com.hanyahunya.gitRemind.util.cookieHeader.SetResultDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
public class AuthCodeServiceImpl implements AuthCodeService {
    private final SendEmailService sendEmailService;
    private final EmailValidateTokenService emailValidateTokenService;

    // Synchronized Map
    private final Map<String, String> authCodeMap = new ConcurrentHashMap<>();
    private ScheduledExecutorService schedule;
    private final AtomicBoolean isSchedulerCreated = new AtomicBoolean(false);
    private final SecureRandom random = new SecureRandom();

    @PostConstruct // Line39のNullPointExceptionを防ぐため
    public void init() {
        schedule = Executors.newScheduledThreadPool(1);
        schedule.shutdown();
    }

    @Override
    public ResponseDto<Void> sendAuthCode(EmailRequestDto emailRequestDto, TokenPurpose tokenPurpose) {
        String email = emailRequestDto.getEmail();

        // スケジューラがない場合、スレッドを作る　+　原子単位のBooleanでシンクロを合わせる
        if(schedule.isShutdown() && isSchedulerCreated.compareAndSet(false, true)) {
            schedule = Executors.newScheduledThreadPool(1);
        }

        String authCode = String.valueOf(random.nextInt(900000) + 100000);
        authCodeMap.put((email + tokenPurpose), authCode);

        //　3分後に実行されるスケジュールを作る（最後のemailが消されたら、スレッドを停止）
        schedule.schedule(() -> {
            authCodeMap.remove((email + tokenPurpose));
            if(authCodeMap.isEmpty()) {
                schedule.shutdown();
                isSchedulerCreated.set(false);
            }
        }, 3, TimeUnit.MINUTES);

        //　ThymeleafのHtmlにパラメータを入れる
        Map<String, Object> params = new HashMap<>();
        params.put("code", authCode);
        boolean success = sendEmailService.sendEmail(email, "gitRemind AuthCode", "kr/test", params);
        if(success) {
            return ResponseDto.success("認証コード発信成功");
        } else {
            return ResponseDto.fail("認証コード発信失敗");
        }
    }

    @Override
    public SetResultDto validateAuthCode(ValidateCodeRequestDto validateCodeRequestDto, TokenPurpose tokenPurpose) {
        String email = validateCodeRequestDto.getEmail();
        String authCode = validateCodeRequestDto.getAuthCode();
        boolean equals = authCode.equals(authCodeMap.get(email + tokenPurpose));
        if(equals) {
            authCodeMap.remove(email);
            String token = emailValidateTokenService.generateToken(email, tokenPurpose);
            return SetResultDto.builder().success(true).validateToken(token).purpose(tokenPurpose).build();
        } else {
            return SetResultDto.builder().build();
        }
    }
}
