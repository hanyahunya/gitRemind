package com.hanyahunya.gitRemind.member.controller;

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
@RequestMapping("/member")
public class AuthController {
    private final AuthCodeService authCodeService;

    @PostMapping("/send-code")
    public ResponseEntity<ResponseDto<Void>> sendAuthCode(@RequestBody @Valid EmailRequestDto emailRequestDto) {
        ResponseDto<Void> responseDto = authCodeService.sendAuthCode(emailRequestDto);
        return toResponse(responseDto);
    }

    @PostMapping("/validate-code")
    public ResponseEntity<ResponseDto<Void>> validateAuthCode(@RequestBody @Valid ValidateCodeRequestDto validateCodeRequestDto) {
        ResponseDto<Void> responseDto = authCodeService.validateAuthCode(validateCodeRequestDto);
        return toResponse(responseDto);
    }
}
