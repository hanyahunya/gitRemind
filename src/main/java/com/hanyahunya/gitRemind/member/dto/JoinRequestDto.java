package com.hanyahunya.gitRemind.member.dto;

import com.hanyahunya.gitRemind.member.entity.Member;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinRequestDto {
    @NotNull
    private String id;
    @NotNull
    private String pw;
    @NotNull
    private String email;
    @NotNull
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
