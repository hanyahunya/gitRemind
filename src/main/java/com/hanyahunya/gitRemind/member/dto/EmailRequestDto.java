package com.hanyahunya.gitRemind.member.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EmailRequestDto {
    @NotNull
    private String email;
}
