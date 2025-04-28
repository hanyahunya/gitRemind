package com.hanyahunya.gitRemind.contribution.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hanyahunya.gitRemind.contribution.entity.Contribution;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GitUsernameRequestDto {
    @JsonIgnore
    private String mid;
    @NotNull
    private String gitUsername;

    public Contribution toEntity() {
        return Contribution.builder()
                .memberId(mid)
                .gitUsername(gitUsername)
                .build();
    }
}
