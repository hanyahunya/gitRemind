package com.hanyahunya.gitRemind.member.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ChangePwRequestDto {
    @JsonIgnore
    private String mid;
    @NotNull
    private String oldPw;
    @NotNull
    private String newPw;
}
