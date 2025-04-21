package com.hanyahunya.gitRemind.contribution.service;

import com.hanyahunya.gitRemind.contribution.dto.AlarmRequestDto;
import com.hanyahunya.gitRemind.contribution.dto.AlarmResponseDto;
import com.hanyahunya.gitRemind.contribution.dto.CommittedResponseDto;
import com.hanyahunya.gitRemind.contribution.dto.GitUsernameRequestDto;
import com.hanyahunya.gitRemind.contribution.entity.Contribution;
import com.hanyahunya.gitRemind.contribution.repository.ContributionRepository;
import com.hanyahunya.gitRemind.contribution.util.AlarmTimeBitConverter;
import com.hanyahunya.gitRemind.util.ResponseDto;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
public class ContributionServiceImpl implements ContributionService {
    private final ContributionRepository contributionRepository;

    @Override
    public ResponseDto<Void> saveOrUpdateGitUsername(GitUsernameRequestDto requestDto) {
        if(contributionRepository.updateContribution(requestDto.toEntity())) {
            return ResponseDto.success("gitユーザー名設定成功");
        } else {
            return ResponseDto.success("gitユーザー名設定失敗");
        }
    }

    @Override
    public ResponseDto<AlarmResponseDto> getAlarm(String mid) {
        return contributionRepository.getContributionByMid(mid)
                .map(contribution -> {
                    Set<Integer> alarmHours = AlarmTimeBitConverter.bitToHourSet(contribution.getAlarmBit());
                    return ResponseDto.success("Alarm読み込み成功", AlarmResponseDto.set(alarmHours));
                })
                .orElseGet(() -> {
                    return ResponseDto.fail("Alarm読み込み失敗");
                });

    }

    @Override
    public ResponseDto<Void> setAlarm(AlarmRequestDto requestDto) {
        Contribution contribution = Contribution.builder()
                .mid(requestDto.getMid())
                .alarmBit(AlarmTimeBitConverter.hourToBit(requestDto.getAlarmHours()))
                .build();
        if (contributionRepository.updateContribution(contribution)) {
            return ResponseDto.success("Alarm設定成功");
        } else {
            return ResponseDto.success("Alarm設定失敗");
        }
    }

    @Override
    public ResponseDto<CommittedResponseDto> getCommitStatus(String mid) {
        return contributionRepository.getContributionByMid(mid)
                .map(contribution -> {
                    return ResponseDto.success("commit読み込み成功", CommittedResponseDto.set(contribution.getCommitted()));
                })
                .orElseGet(() -> {
                    return ResponseDto.fail("commit読み込み失敗");
                });
    }
}
