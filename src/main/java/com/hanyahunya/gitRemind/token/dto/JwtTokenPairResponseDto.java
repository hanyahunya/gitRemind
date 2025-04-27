package com.hanyahunya.gitRemind.token.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "set")
public class JwtTokenPairResponseDto {
    private String accessToken;
    private String refreshToken;
}
