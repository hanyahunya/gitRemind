package com.hanyahunya.gitRemind.member.controller;

import com.hanyahunya.gitRemind.ResponseDto;
import com.hanyahunya.gitRemind.member.dto.JoinRequestDto;
import com.hanyahunya.gitRemind.member.dto.JwtResponseDto;
import com.hanyahunya.gitRemind.member.dto.LoginRequestDto;
import com.hanyahunya.gitRemind.member.dto.MemberInfoResponseDto;
import com.hanyahunya.gitRemind.member.service.MemberService;
import com.hanyahunya.gitRemind.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/join")
    public ResponseEntity<ResponseDto<JwtResponseDto>> join(@RequestBody @Valid JoinRequestDto joinRequestDto) {
        ResponseDto<JwtResponseDto> responseDto = memberService.join(joinRequestDto);
        return response(responseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDto<JwtResponseDto>> login(@RequestBody @Valid LoginRequestDto loginRequestDto) {
        ResponseDto<JwtResponseDto> responseDto = memberService.login(loginRequestDto);
        return response(responseDto);
    }

    @GetMapping("/info")
    public ResponseEntity<ResponseDto<MemberInfoResponseDto>> memberInfo(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        ResponseDto<MemberInfoResponseDto> responseDto = memberService.getInfo(userPrincipal.getMid());
        return response(responseDto);
    }

    private <T> ResponseEntity<ResponseDto<T>> response(ResponseDto<T> responseDto) {
        if (responseDto.isSuccess()) {
            return ResponseEntity.ok().body(responseDto);
        } else {
            return ResponseEntity.badRequest().body(responseDto);
        }
    }
}
