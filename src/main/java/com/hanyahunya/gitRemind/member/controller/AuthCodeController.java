package com.hanyahunya.gitRemind.member.controller;

import com.hanyahunya.gitRemind.token.service.TokenPurpose;
import com.hanyahunya.gitRemind.util.ResponseDto;
import com.hanyahunya.gitRemind.member.dto.EmailRequestDto;
import com.hanyahunya.gitRemind.member.dto.ValidateCodeRequestDto;
import com.hanyahunya.gitRemind.member.service.AuthCodeService;
import com.hanyahunya.gitRemind.util.cookieHeader.SetResultDto;
import com.hanyahunya.gitRemind.util.cookieHeader.TokenCookieHeaderGenerator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.hanyahunya.gitRemind.util.ResponseUtil.toResponse;
import static com.hanyahunya.gitRemind.util.ResponseUtil.toResponseWithHeader;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth-code")
public class AuthCodeController {
    private final AuthCodeService authCodeService;
    private final TokenCookieHeaderGenerator tokenCookieHeaderGenerator;


    @PostMapping
    public ResponseEntity<ResponseDto<Void>> send(@RequestBody @Valid EmailRequestDto emailRequestDto) {
        ResponseDto<Void> responseDto = authCodeService.sendAuthCode(emailRequestDto, TokenPurpose.EMAIL_VERIFICATION);
        return toResponse(responseDto);
    }

    @PostMapping("/validate")
    public ResponseEntity<ResponseDto<Void>> validate(@RequestBody @Valid ValidateCodeRequestDto validateCodeRequestDto) {
        SetResultDto setResultDto = authCodeService.validateAuthCode(validateCodeRequestDto, TokenPurpose.EMAIL_VERIFICATION);
        if (setResultDto.isSuccess()) {
            HttpHeaders headers = tokenCookieHeaderGenerator.handleTokenHeader(setResultDto);
            return toResponseWithHeader(ResponseDto.success("メール認証確認トークン発行成功"), headers);
        } else {
            return toResponse(ResponseDto.fail("メール認証確認トークン発行失敗"));
        }
    }

    @PostMapping("/password-code")
    public ResponseEntity<ResponseDto<Void>> sendPasswordCode(@RequestBody @Valid EmailRequestDto emailRequestDto) {
        ResponseDto<Void> responseDto = authCodeService.sendAuthCode(emailRequestDto, TokenPurpose.RESET_PASSWORD);
        return toResponse(responseDto);
    }

    @PostMapping("/validate/password-code")
    public ResponseEntity<ResponseDto<Void>> validatePwCode(@RequestBody @Valid ValidateCodeRequestDto validateCodeRequestDto) {
        SetResultDto setResultDto = authCodeService.validateAuthCode(validateCodeRequestDto, TokenPurpose.RESET_PASSWORD);
        if (setResultDto.isSuccess()) {
            HttpHeaders headers = tokenCookieHeaderGenerator.handleTokenHeader(setResultDto);
            return toResponseWithHeader(ResponseDto.success("パスワード更新用トークン発行成功"), headers);
        } else {
            return toResponse(ResponseDto.fail("パスワード更新用トークン発行失敗"));
        }
    }
}
