package com.hanyahunya.gitRemind.token.service;

import com.hanyahunya.gitRemind.token.dto.JwtTokenPairResponseDto;
import com.hanyahunya.gitRemind.token.dto.RefreshAccessTokenRequestDto;
import com.hanyahunya.gitRemind.util.ResponseDto;
import org.springframework.http.HttpHeaders;

public interface TokenService {
    ResponseDto<JwtTokenPairResponseDto> issueTokens(String memberId);

    public HttpHeaders refreshAccessToken(String refreshToken, String oldAccessToken);

}
