package com.hanyahunya.gitRemind.member.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Member {
    private String memberId; // UUID-char(36) or UUID-Binary(16)
    private String loginId;
    private String password;
    private String email;
    private String country; // ISO 3166-1 Alpha-2
}
