package com.hanyahunya.gitRemind.member.controller;

import com.hanyahunya.gitRemind.util.TokenCookieHeaderGenerator;
import com.hanyahunya.gitRemind.token.dto.JwtTokenPairResponseDto;
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
    private final TokenCookieHeaderGenerator tokenCookieHeaderGenerator;

    @PostMapping("/join")
    public ResponseEntity<ResponseDto<Void>> join(@RequestBody @Valid JoinRequestDto joinRequestDto) {
        ResponseDto<Void> responseDto = memberService.join(joinRequestDto);
        return toResponse(responseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDto<Void>> login(@RequestBody @Valid LoginRequestDto loginRequestDto) {
        ResponseDto<JwtTokenPairResponseDto> responseDto = memberService.login(loginRequestDto);
        if (responseDto.isSuccess()) {
            String accessTokenHeader = tokenCookieHeaderGenerator.buildByAccessToken(responseDto.getData().getAccessToken());
            String refreshTokenHeader = tokenCookieHeaderGenerator.buildByRefreshToken(responseDto.getData().getRefreshToken());
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.SET_COOKIE, accessTokenHeader);
            headers.add(HttpHeaders.SET_COOKIE, refreshTokenHeader);
            return toResponseWithHeader(ResponseDto.success(responseDto.getMessage()), headers);
        } else {
            return toResponse(ResponseDto.fail(responseDto.getMessage()));
        }
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
        ResponseDto<Void> responseDto = memberService.deleteMember(requestDto);
        return toResponse(responseDto);
    }
}
