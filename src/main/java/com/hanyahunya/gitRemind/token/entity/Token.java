package com.hanyahunya.gitRemind.token.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Builder
@Getter @Setter
public class Token {
    private String token_id;
    private String access_token;
    private String refresh_token;
    private Date access_token_expiry;
    private Date refresh_token_expiry;
}
