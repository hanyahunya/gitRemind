package com.hanyahunya.gitRemind.member.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hanyahunya.gitRemind.member.entity.Member;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DeleteMemberRequestDto {
    @JsonIgnore
    private String memberId;
    // 後に違う認証方法で変える予定
    private String loginId;
    private String password;

    public Member toEntity() {
        return Member.builder()
                .memberId(memberId)
                .loginId(loginId)
                .password(password)
                .build();
    }
}
