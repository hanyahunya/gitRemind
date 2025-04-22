package com.hanyahunya.gitRemind.contribution.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Contribution {
    private String mid;
    private String gitUsername;
    @Builder.Default
    private int alarmBit = -1;
    private Boolean committed;

    // for scheduler only
    private String email;
}
