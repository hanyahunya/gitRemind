package com.hanyahunya.gitRemind.token.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RefreshAccessTokenRequestDto {
    private String accessToken;
    private String refreshToken;
}
