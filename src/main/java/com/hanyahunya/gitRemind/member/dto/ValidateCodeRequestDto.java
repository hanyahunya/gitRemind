package com.hanyahunya.gitRemind.member.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ValidateCodeRequestDto {
    @NotNull
    private String email;
    @NotNull
    private String authCode;
}
