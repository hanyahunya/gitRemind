package com.hanyahunya.gitRemind.member.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hanyahunya.gitRemind.member.entity.Member;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateMemberRequestDto {
    @JsonIgnore
    private String mid;
    private String id;
    private String pw;
    private String email;

    public Member toEntity() {
        return Member.builder()
                .mid(mid)
                .id(id)
                .pw(pw)
                .email(email)
                .build();
    }
}
