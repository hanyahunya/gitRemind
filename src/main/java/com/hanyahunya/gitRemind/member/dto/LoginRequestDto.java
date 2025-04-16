package com.hanyahunya.gitRemind.member.dto;

import com.hanyahunya.gitRemind.member.entity.Member;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoginRequestDto {
    @NotNull
    private String id;
    @NotNull
    private String pw;

    public Member dtoToEntity() {
        return Member.builder()
                .id(id)
                .pw(pw)
                .build();
    }
}
