package com.hanyahunya.gitRemind.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "set")
public class JwtResponseDto {
    private String token;
}
