package com.hanyahunya.gitRemind.member.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hanyahunya.gitRemind.member.entity.Member;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateMemberRequestDto {
    @JsonIgnore
    private String memberId;
    private String loginId;
    private String password;
    private String email;
    private String country;

    public Member toEntity() {
        return Member.builder()
                .memberId(memberId)
                .loginId(loginId)
                .email(email)
                .country(country)
                .build();
    }
}
