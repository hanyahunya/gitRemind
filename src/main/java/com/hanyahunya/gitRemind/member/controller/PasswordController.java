package com.hanyahunya.gitRemind.member.controller;

import com.hanyahunya.gitRemind.member.dto.LoginRequestDto;
import com.hanyahunya.gitRemind.member.dto.ResetPwRequestDto;
import com.hanyahunya.gitRemind.member.service.PasswordService;
import com.hanyahunya.gitRemind.util.ResponseDto;
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
public class PasswordController {
    private  final PasswordService passwordService;

    @PostMapping("/resetPassword")
    public ResponseEntity<ResponseDto<Void>> resetPassword(@RequestBody @Valid ResetPwRequestDto resetPwRequestDto) {
        ResponseDto<Void> responseDto = passwordService.forgotPassword(resetPwRequestDto);
        return toResponse(responseDto);
    }
}
