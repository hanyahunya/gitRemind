package com.hanyahunya.gitRemind.member.dto;

import com.hanyahunya.gitRemind.entity.Member;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinMemberDto {
    private String id;
    private String pw;
    private String email;
    private String git_addr;

    public Member dtoToEntity() {
        return Member.builder()
                .id(this.id)
                .pw(this.pw)
                .email(this.email)
                .git_addr(this.git_addr)
                .build();
    }
}
