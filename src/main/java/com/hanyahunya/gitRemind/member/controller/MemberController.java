package com.hanyahunya.gitRemind.member.controller;

import com.hanyahunya.gitRemind.util.ResponseDto;
import com.hanyahunya.gitRemind.member.dto.*;
import com.hanyahunya.gitRemind.member.service.MemberService;
import com.hanyahunya.gitRemind.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static com.hanyahunya.gitRemind.util.ResponseUtil.toResponse;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/join")
    public ResponseEntity<ResponseDto<JwtResponseDto>> join(@RequestBody @Valid JoinRequestDto joinRequestDto) {
        ResponseDto<JwtResponseDto> responseDto = memberService.join(joinRequestDto);
        return toResponse(responseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDto<JwtResponseDto>> login(@RequestBody @Valid LoginRequestDto loginRequestDto) {
        ResponseDto<JwtResponseDto> responseDto = memberService.login(loginRequestDto);
        return toResponse(responseDto);
    }

    @GetMapping("/info")
    public ResponseEntity<ResponseDto<MemberInfoResponseDto>> memberInfo(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        ResponseDto<MemberInfoResponseDto> responseDto = memberService.getInfo(userPrincipal.getMid());
        return toResponse(responseDto);
    }

    @PatchMapping("/update")
    public ResponseEntity<ResponseDto<Void>> update(@RequestBody @Valid UpdateMemberRequestDto requestDto, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        requestDto.setMid(userPrincipal.getMid());
        ResponseDto<Void> responseDto = memberService.updateMember(requestDto);
        return toResponse(responseDto);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDto<Void>> delete(@RequestBody @Valid DeleteMemberRequestDto requestDto, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        requestDto.setMid(userPrincipal.getMid());
        ResponseDto<Void> responseDto = memberService.deleteMember(requestDto);
        return toResponse(responseDto);
    }
}
