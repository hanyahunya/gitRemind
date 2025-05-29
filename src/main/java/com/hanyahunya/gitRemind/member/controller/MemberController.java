package com.hanyahunya.gitRemind.member.controller;

import com.hanyahunya.gitRemind.token.service.TokenService;
import com.hanyahunya.gitRemind.util.cookieHeader.SetResultDto;
import com.hanyahunya.gitRemind.util.cookieHeader.TokenCookieHeaderGenerator;
import com.hanyahunya.gitRemind.util.ResponseDto;
import com.hanyahunya.gitRemind.member.dto.*;
import com.hanyahunya.gitRemind.member.service.MemberService;
import com.hanyahunya.gitRemind.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static com.hanyahunya.gitRemind.util.ResponseUtil.toResponse;
import static com.hanyahunya.gitRemind.util.ResponseUtil.toResponseWithHeader;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
    private final TokenService tokenService;
    private final TokenCookieHeaderGenerator tokenCookieHeaderGenerator;

    @PostMapping("/join")
    public ResponseEntity<ResponseDto<Void>> join(@RequestBody @Valid JoinRequestDto joinRequestDto, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        joinRequestDto.setEmail(userPrincipal.getEmail());
        ResponseDto<Void> responseDto = memberService.join(joinRequestDto);
        return toResponse(responseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDto<Void>> login(@RequestBody @Valid LoginRequestDto loginRequestDto) {
        SetResultDto setResultDto = memberService.login(loginRequestDto);
        if (setResultDto.isSuccess()) {
            HttpHeaders headers = tokenCookieHeaderGenerator.handleTokenHeader(setResultDto);
            return toResponseWithHeader(ResponseDto.success("ログイン成功"), headers);
        } else {
            return toResponse(ResponseDto.fail("ログイン失敗"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseDto<Void>> logout(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        SetResultDto setResultDto = tokenService.deleteTokenCurrentDevice(userPrincipal.getTokenId());
        HttpHeaders headers = tokenCookieHeaderGenerator.handleTokenHeader(setResultDto);
        return toResponseWithHeader(ResponseDto.success("ログアウト成功"), headers);
    }

    @GetMapping("/info")
    public ResponseEntity<ResponseDto<MemberInfoResponseDto>> memberInfo(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        ResponseDto<MemberInfoResponseDto> responseDto = memberService.getInfo(userPrincipal.getMemberId());
        return toResponse(responseDto);
    }

    @PatchMapping("/update")
    public ResponseEntity<ResponseDto<Void>> update(@RequestBody @Valid UpdateMemberRequestDto requestDto, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        requestDto.setMemberId(userPrincipal.getMemberId());
        ResponseDto<Void> responseDto = memberService.updateMember(requestDto);
        return toResponse(responseDto);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDto<Void>> delete(@RequestBody @Valid DeleteMemberRequestDto requestDto, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        requestDto.setMemberId(userPrincipal.getMemberId());
        SetResultDto setResultDto = memberService.deleteMember(requestDto);
        if (setResultDto.isSuccess()) {
            HttpHeaders headers = tokenCookieHeaderGenerator.handleTokenHeader(setResultDto);
            return toResponseWithHeader(ResponseDto.success("ユーザー退会成功"), headers);
        }
        return toResponse(ResponseDto.fail("ユーザー退会失敗"));
    }
}
