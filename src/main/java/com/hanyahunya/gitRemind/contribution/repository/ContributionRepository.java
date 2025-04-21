package com.hanyahunya.gitRemind.contribution.repository;

import com.hanyahunya.gitRemind.contribution.entity.Contribution;

import java.util.Optional;

public interface ContributionRepository {
//    boolean setGitUsername(Contribution contribution);

    Optional<Contribution> getContributionByMid(String mid);

    Optional<Contribution> getContributionByGitUsername(String gitUsername);

    boolean updateContribution(Contribution contribution);
}
