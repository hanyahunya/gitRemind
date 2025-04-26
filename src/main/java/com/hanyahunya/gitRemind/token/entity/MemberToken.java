package com.hanyahunya.gitRemind.token.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class MemberToken {
    private String memberId;
    private String tokenId;
}
