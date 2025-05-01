package com.hanyahunya.gitRemind.token.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Builder
@Getter @Setter
public class Token {
    private String tokenId;
    private String accessToken;
    private String refreshToken;
    private Date accessTokenExpiry;
    private Date refreshTokenExpiry;
}
