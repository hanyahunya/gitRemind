package com.hanyahunya.gitRemind.member.controller;

import com.hanyahunya.gitRemind.member.dto.ChangePwRequestDto;
import com.hanyahunya.gitRemind.member.dto.ResetPwRequestDto;
import com.hanyahunya.gitRemind.member.service.PasswordService;
import com.hanyahunya.gitRemind.security.UserPrincipal;
import com.hanyahunya.gitRemind.util.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.hanyahunya.gitRemind.util.ResponseUtil.toResponse;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class PasswordController {
    private final PasswordService passwordService;

    /**
     * @param userPrincipal /auth-code/validate/password-codeから発行した使い捨てのJwtTokenが必要
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ResponseDto<Void>> resetPassword(@RequestBody @Valid ResetPwRequestDto resetPwRequestDto, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        resetPwRequestDto.setEmail(userPrincipal.getEmail());
        ResponseDto<Void> responseDto = passwordService.forgotPassword(resetPwRequestDto);
        return toResponse(responseDto);
    }

    @PostMapping("/change-password")
    public ResponseEntity<ResponseDto<Void>> changePassword(@RequestBody @Valid ChangePwRequestDto changePwRequestDto, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        changePwRequestDto.setTokenId(userPrincipal.getTokenId());
        changePwRequestDto.setMemberId(userPrincipal.getMemberId());
        ResponseDto<Void> responseDto = passwordService.changePassword(changePwRequestDto);
        return toResponse(responseDto);
    }
}
