package com.hanyahunya.gitRemind.member.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ResetPwRequestDto {
    @NotNull
    private String email;
    @NotNull
    private String token;
    @NotNull
    private String newPw;
}
