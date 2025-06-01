package com.hanyahunya.gitRemind.contribution.service;

import com.hanyahunya.gitRemind.contribution.dto.*;
import com.hanyahunya.gitRemind.util.ResponseDto;

public interface ContributionService {
    ResponseDto<Void> saveOrUpdateGitUsername(GitUsernameRequestDto requestDto);

    ResponseDto<AlarmResponseDto> getAlarm(String mid);

    ResponseDto<Void> setAlarm(AlarmRequestDto requestDto);

    ResponseDto<CommittedResponseDto> getCommitStatus(String mid);

    ResponseDto<GitUsernameResponseDto> getGitUsername(String memberId);
}