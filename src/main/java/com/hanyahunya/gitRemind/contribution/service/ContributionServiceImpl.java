package com.hanyahunya.gitRemind.contribution.service;

import com.hanyahunya.gitRemind.contribution.dto.*;
import com.hanyahunya.gitRemind.contribution.entity.Contribution;
import com.hanyahunya.gitRemind.contribution.repository.ContributionRepository;
import com.hanyahunya.gitRemind.contribution.util.AlarmTimeBitConverter;
import com.hanyahunya.gitRemind.infrastructure.github.GithubHtmlScraper;
import com.hanyahunya.gitRemind.infrastructure.github.GithubUserValidator;
import com.hanyahunya.gitRemind.util.ResponseDto;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
public class ContributionServiceImpl implements ContributionService {
    private final ContributionRepository contributionRepository;
    private final GithubHtmlScraper githubHtmlScraper;

    @Override
    public ResponseDto<Void> saveOrUpdateGitUsername(GitUsernameRequestDto requestDto) {
        if(GithubUserValidator.isValid(requestDto.getGitUsername())) {
            if(contributionRepository.updateContribution(requestDto.toEntity())) {
                return ResponseDto.success("gitユーザー名設定成功");
            } else {
                return ResponseDto.success("gitユーザー名設定失敗");
            }
        } else {
            return ResponseDto.fail("check git username");
        }
    }

    @Override
    public ResponseDto<AlarmResponseDto> getAlarm(String memberId) {
        return contributionRepository.getContributionByMemberId(memberId)
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
                .memberId(requestDto.getMid())
                .alarmBit(AlarmTimeBitConverter.hourToBit(requestDto.getAlarmHours()))
                .build();
        if (contributionRepository.updateContribution(contribution)) {
            return ResponseDto.success("Alarm設定成功");
        } else {
            return ResponseDto.success("Alarm設定失敗");
        }
    }

    @Override
    public ResponseDto<CommittedResponseDto> getCommitStatus(String memberId) {
        return contributionRepository.getContributionByMemberId(memberId)
                .map(contribution -> {
                    boolean committed = false;
                    if (contribution.getCommitted()) {
                        committed = true;
                    } else {
                        int todayContributionCount = githubHtmlScraper.getTodayContributionCount(contribution.getGitUsername());
                        if (todayContributionCount > 0) {
                            committed = true;
                        }
                    }
                    return ResponseDto.success("commit読み込み成功", CommittedResponseDto.set(committed));
                })
                .orElseGet(() -> {
                    return ResponseDto.fail("commit読み込み失敗");
                });
    }

    @Override
    public ResponseDto<GitUsernameResponseDto> getGitUsername(String memberId) {
        return contributionRepository.getContributionByMemberId(memberId)
                .map(contribution -> {
                    return ResponseDto.success("gitユーザ名読み込み成功", GitUsernameResponseDto.set(contribution.getGitUsername()));
                })
                .orElseGet(() -> {
                    return ResponseDto.fail("gitユーザ名読み込み失敗");
                });
    }
}
