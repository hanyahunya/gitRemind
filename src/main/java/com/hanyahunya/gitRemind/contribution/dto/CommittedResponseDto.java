package com.hanyahunya.gitRemind.contribution.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "set")
public class CommittedResponseDto {
    private boolean committed; // 나중에 오늘한 커밋수같이 추가
}
