package com.hanyahunya.gitRemind.member.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hanyahunya.gitRemind.member.entity.Member;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinRequestDto {
    @NotNull
    private String loginId;
    @NotNull
    private String password;
    @JsonIgnore
    private String email;
    private String country;

    public Member dtoToEntity() {
        return Member.builder()
                .loginId(this.loginId)
                .password(this.password)
                .email(this.email)
                .country(this.country)
                .build();
    }
}
