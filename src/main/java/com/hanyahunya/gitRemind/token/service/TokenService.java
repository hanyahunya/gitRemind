package com.hanyahunya.gitRemind.token.service;

import com.hanyahunya.gitRemind.token.dto.JwtTokenPairResponseDto;
import com.hanyahunya.gitRemind.token.dto.RefreshAccessTokenRequestDto;
import com.hanyahunya.gitRemind.util.ResponseDto;

public interface TokenService {
    ResponseDto<JwtTokenPairResponseDto> issueTokens(String memberId);

    String refreshAccessToken(RefreshAccessTokenRequestDto requestDto);


}
