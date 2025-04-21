package com.hanyahunya.gitRemind.contribution.service;

import com.hanyahunya.gitRemind.contribution.dto.AlarmRequestDto;
import com.hanyahunya.gitRemind.contribution.dto.AlarmResponseDto;
import com.hanyahunya.gitRemind.contribution.dto.CommittedResponseDto;
import com.hanyahunya.gitRemind.contribution.dto.GitUsernameRequestDto;
import com.hanyahunya.gitRemind.util.ResponseDto;

public interface ContributionService {
    ResponseDto<Void> saveOrUpdateGitUsername(GitUsernameRequestDto requestDto);

    ResponseDto<AlarmResponseDto> getAlarm(String mid);

    ResponseDto<Void> setAlarm(AlarmRequestDto requestDto);

    ResponseDto<CommittedResponseDto> getCommitStatus(String mid);
}