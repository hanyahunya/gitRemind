package com.hanyahunya.gitRemind.member.controller;

import com.hanyahunya.gitRemind.member.dto.JwtResponseDto;
import com.hanyahunya.gitRemind.member.entity.Member;
import com.hanyahunya.gitRemind.member.service.PwTokenService;
import com.hanyahunya.gitRemind.util.ResponseDto;
import com.hanyahunya.gitRemind.member.dto.EmailRequestDto;
import com.hanyahunya.gitRemind.member.dto.ValidateCodeRequestDto;
import com.hanyahunya.gitRemind.member.service.AuthCodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.hanyahunya.gitRemind.util.ResponseUtil.toResponse;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth-code")
public class AuthCodeController {
    private final AuthCodeService authCodeService;
    private final PwTokenService pwTokenService;

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

    @PostMapping("/validate/pw-code")
    public ResponseEntity<ResponseDto<JwtResponseDto>> validatePwCode(@RequestBody @Valid ValidateCodeRequestDto validateCodeRequestDto) {
        if (authCodeService.validateAuthCode(validateCodeRequestDto).isSuccess()) {
            String token = pwTokenService.generateToken(validateCodeRequestDto.getEmail());
            ResponseDto<JwtResponseDto> responseDto = ResponseDto.success("success", JwtResponseDto.set(token));
            return ResponseEntity.ok(responseDto);
        } else {
            return ResponseEntity.status(401).build();
        }
    }
}
