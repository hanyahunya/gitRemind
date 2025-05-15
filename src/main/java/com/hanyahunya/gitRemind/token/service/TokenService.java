package com.hanyahunya.gitRemind.token.service;

import com.hanyahunya.gitRemind.token.dto.JwtTokenPairResponseDto;
import com.hanyahunya.gitRemind.util.ResponseDto;
import com.hanyahunya.gitRemind.util.cookieHeader.SetResultDto;

public interface TokenService {
    ResponseDto<JwtTokenPairResponseDto> issueTokens(String memberId);

    SetResultDto refreshAccessToken(String oldAccessToken, String refreshToken);

    ResponseDto<Void> deleteTokenAtAllDevice(String memberId);

    SetResultDto deleteTokenCurrentDevice(String tokenId);

    void deleteTokenOtherDevice(String memberId, String tokenId);
}
