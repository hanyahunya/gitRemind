package com.hanyahunya.gitRemind.contribution.repository;

import com.hanyahunya.gitRemind.contribution.entity.Contribution;

import java.util.List;
import java.util.Optional;

public interface ContributionRepository {
//    boolean setGitUsername(Contribution contribution);

    Optional<Contribution> getContributionByMemberId(String memberId);

    Optional<Contribution> getContributionByGitUsername(String gitUsername);

    boolean updateContribution(Contribution contribution);

    // !!! for Scheduler dont use スケジューラ以外では使わない !!!
    List<Contribution> findAllContributions();

    void resetIsTodayCommitted();
    }
