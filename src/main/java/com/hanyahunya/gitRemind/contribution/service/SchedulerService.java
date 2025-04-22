package com.hanyahunya.gitRemind.contribution.service;

import com.hanyahunya.gitRemind.contribution.entity.Contribution;
import com.hanyahunya.gitRemind.contribution.repository.ContributionRepository;
import com.hanyahunya.gitRemind.contribution.util.AlarmTimeBitConverter;
import com.hanyahunya.gitRemind.infrastructure.email.SendEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class SchedulerService {
    private final ContributionRepository contributionRepository;
    private final SendEmailService sendEmailService;

    /**
     * cron表現式
     * “秒、分、時、日、月、曜日(MON,THU,WED...or MON-FRI)”
     */
    @Scheduled(cron = "0 0 * * * *")
    public void checkContributionEveryHour() {
        int hour = LocalDateTime.now().getHour();
        List<Contribution> contributionList = contributionRepository.findAllContributions();
        for(Contribution contribution : contributionList) {

            if(AlarmTimeBitConverter.bitToHourSet(contribution.getAlarmBit()).contains(hour)) {

                // todo find isCommitted from github api ->　update is_committed

                Map<String, Object> params = new HashMap<>();
                params.put("username", contribution.getGitUsername());
                String subject = contribution.getGitUsername() + "님 아직 커밋을 안하셨어요!"; // 一応、韓国語
                sendEmailService.sendEmail(contribution.getEmail(), subject, "gitRemind", params);
            }
        }
    }
}
