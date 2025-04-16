package com.hanyahunya.gitRemind.member.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Member {
    private String mid; // UUID-char(36) or UUID-Binary(16)
    private String id;
    private String pw;
    private String email;
    private String git_addr;
}
