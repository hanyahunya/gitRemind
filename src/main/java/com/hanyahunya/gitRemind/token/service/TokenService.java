package com.hanyahunya.gitRemind.token.service;

import com.hanyahunya.gitRemind.token.dto.JwtTokenPairResponseDto;
import com.hanyahunya.gitRemind.token.dto.RefreshAccessTokenRequestDto;
import com.hanyahunya.gitRemind.util.ResponseDto;
import com.hanyahunya.gitRemind.util.cookieHeader.SetResultDto;
import org.springframework.http.HttpHeaders;

public interface TokenService {
    ResponseDto<JwtTokenPairResponseDto> issueTokens(String memberId);

    SetResultDto refreshAccessToken(String refreshToken, String oldAccessToken);

    public ResponseDto<Void> deleteTokenAtAllDevice(String memberId);
}
