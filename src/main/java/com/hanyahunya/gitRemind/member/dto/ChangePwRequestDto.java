package com.hanyahunya.gitRemind.member.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ChangePwRequestDto {
    @JsonIgnore
    private String tokenId;
    @JsonIgnore
    private String memberId;
    @NotNull
    private String oldPassword;
    @NotNull
    private String newPassword;
}
