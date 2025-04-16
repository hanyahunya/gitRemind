package com.hanyahunya.gitRemind.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "set")
public class MemberInfoResponseDto {
//    private String id;
    private String email;
    private String git_addr;
}
