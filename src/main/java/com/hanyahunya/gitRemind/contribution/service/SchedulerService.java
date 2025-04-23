package com.hanyahunya.gitRemind.contribution.service;

import com.hanyahunya.gitRemind.contribution.entity.Contribution;
import com.hanyahunya.gitRemind.contribution.repository.ContributionRepository;
import com.hanyahunya.gitRemind.contribution.util.AlarmTimeBitConverter;
import com.hanyahunya.gitRemind.infrastructure.email.SendEmailService;
import com.hanyahunya.gitRemind.infrastructure.github.GithubHtmlScraper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
@RequiredArgsConstructor
public class SchedulerService {
    private final ContributionRepository contributionRepository;
    private final SendEmailService sendEmailService;
    private final GithubHtmlScraper githubHtmlScraper;

    /**
     * cron表現式
     * “秒、分、時、日、月、曜日(MON,THU,WED...or MON-FRI)”
     */
    @Scheduled(cron = "0 0 * * * *")
    public void checkContributionEveryHour() {
        int hour = LocalDateTime.now().getHour();
        List<Contribution> contributionList = contributionRepository.findAllContributions();

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        List<Future<Void>> futures = new ArrayList<>();

        for (Contribution contribution : contributionList) {
            futures.add(executorService.submit(() -> {
                try {
                    if (AlarmTimeBitConverter.bitToHourSet(contribution.getAlarmBit()).contains(hour)) {
                        if (githubHtmlScraper.getTodayContributionCount(contribution.getGitUsername()) > 0) {
                            Contribution updateContribution = Contribution.builder()
                                    .mid(contribution.getMid())
                                    .committed(true)
                                    .build();
                            contributionRepository.updateContribution(updateContribution);
                        } else {
                            Map<String, Object> params = new HashMap<>();
                            params.put("username", contribution.getGitUsername());
                            String subject = contribution.getGitUsername() + "님 아직 커밋을 안하셨어요!";
                            sendEmailService.sendEmail(contribution.getEmail(), subject, "gitRemind", params);
                        }
                    }
                } catch (Exception e) {
                    log.warn("{}-{}", this.getClass().getSimpleName(), e.getClass().getSimpleName());
                }
                return null;
            }));
        }

        for (Future<Void> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                log.warn("{}-{}", this.getClass().getSimpleName(), e.getClass().getSimpleName());
            }
        }

        executorService.shutdown();  // 종료
    }

}
