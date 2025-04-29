package com.hanyahunya.gitRemind.member.controller;

import com.hanyahunya.gitRemind.member.dto.JwtResponseDto;
import com.hanyahunya.gitRemind.token.service.PwTokenService;
import com.hanyahunya.gitRemind.util.ResponseDto;
import com.hanyahunya.gitRemind.member.dto.EmailRequestDto;
import com.hanyahunya.gitRemind.member.dto.ValidateCodeRequestDto;
import com.hanyahunya.gitRemind.member.service.AuthCodeService;
import jakarta.servlet.http.Cookie;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
    private final PwTokenService pwTokenService;

    @Value("${jwt.pwToken.expiration}")
    private long expirationTime;

    @PostMapping
    public ResponseEntity<ResponseDto<Void>> send(@RequestBody @Valid EmailRequestDto emailRequestDto) {
        ResponseDto<Void> responseDto = authCodeService.sendAuthCode(emailRequestDto);
        return toResponse(responseDto);
    }

    @PostMapping("/validate")
    public ResponseEntity<ResponseDto<Void>> validate(@RequestBody @Valid ValidateCodeRequestDto validateCodeRequestDto) {
        ResponseDto<Void> responseDto = authCodeService.validateAuthCode(validateCodeRequestDto);
        return toResponse(responseDto);
    }

    @PostMapping("/validate/password-code")
    public ResponseEntity<ResponseDto<Void>> validatePwCode(@RequestBody @Valid ValidateCodeRequestDto validateCodeRequestDto) {
        if (authCodeService.validateAuthCode(validateCodeRequestDto).isSuccess()) {
            String token = pwTokenService.generateToken(validateCodeRequestDto.getEmail());
            HttpHeaders headers = buildTokenCookieHeader(token);
            return toResponseWithHeader(ResponseDto.success("パスワード更新用トークン発行成功"), headers);
        } else {
            return toResponse(ResponseDto.fail("パスワード更新用トークン発行失敗"));
        }
    }

    private HttpHeaders buildTokenCookieHeader(String token) {
        Cookie cookie = new Cookie("reset_password_token", token);
        cookie.setPath("/");
        cookie.setMaxAge((int) (expirationTime / 1000));
        String format = String.format(
                "%s=%s; Max-Age=%d; Path=%s; SameSite=Lax; httpOnly;",
                cookie.getName(),
                cookie.getValue(),
                cookie.getMaxAge(),
                cookie.getPath()
        );
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, format);
        return headers;
    }

}
