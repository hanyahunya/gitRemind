package com.hanyahunya.gitRemind.contribution.controller;

import com.hanyahunya.gitRemind.contribution.dto.AlarmRequestDto;
import com.hanyahunya.gitRemind.contribution.dto.AlarmResponseDto;
import com.hanyahunya.gitRemind.contribution.dto.CommittedResponseDto;
import com.hanyahunya.gitRemind.contribution.dto.GitUsernameRequestDto;
import com.hanyahunya.gitRemind.contribution.service.ContributionService;
import com.hanyahunya.gitRemind.security.UserPrincipal;
import com.hanyahunya.gitRemind.util.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.hanyahunya.gitRemind.util.ResponseUtil.toResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contributions")
public class ContributionController {
    private final ContributionService contributionService;

    @PutMapping("/git-username")
    public ResponseEntity<ResponseDto<Void>> saveOrUpdateGitUsername(@RequestBody @Valid GitUsernameRequestDto requestDto, @AuthenticationPrincipal UserPrincipal user) {
        requestDto.setMemberId(user.getMemberId());
        ResponseDto<Void> responseDto = contributionService.saveOrUpdateGitUsername(requestDto);
        return toResponse(responseDto);
    }

    @GetMapping("/alarm")
    public ResponseEntity<ResponseDto<AlarmResponseDto>> getAlarm(@AuthenticationPrincipal UserPrincipal user) {
        ResponseDto<AlarmResponseDto> responseDto = contributionService.getAlarm(user.getMemberId());
        return toResponse(responseDto);
    }
    @PatchMapping("/alarm")
    public ResponseEntity<ResponseDto<Void>> setAlarm(@RequestBody @Valid AlarmRequestDto requestDto, @AuthenticationPrincipal UserPrincipal user) {
        requestDto.setMid(user.getMemberId());
        ResponseDto<Void> responseDto = contributionService.setAlarm(requestDto);
        return toResponse(responseDto);
    }

    @GetMapping("/status")
    public ResponseEntity<ResponseDto<CommittedResponseDto>> getStatus(@AuthenticationPrincipal UserPrincipal user) {
        ResponseDto<CommittedResponseDto> responseDto = contributionService.getCommitStatus(user.getMemberId());
        return toResponse(responseDto);
    }
}
