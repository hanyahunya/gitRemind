package com.hanyahunya.gitRemind.token.service;

import com.hanyahunya.gitRemind.token.dto.JwtTokenPairResponseDto;
import com.hanyahunya.gitRemind.token.dto.RefreshAccessTokenRequestDto;
import com.hanyahunya.gitRemind.token.entity.Token;
import com.hanyahunya.gitRemind.token.repository.MemberTokenRepository;
import com.hanyahunya.gitRemind.token.repository.TokenRepository;
import com.hanyahunya.gitRemind.util.ResponseDto;
import com.hanyahunya.gitRemind.util.service.EncodeService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.UUID;

@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final TokenRepository tokenRepository;
    private final MemberTokenRepository memberTokenRepository;
    private final AccessTokenService accessTokenService;
    private final RefreshTokenService refreshTokenService;
    private final EncodeService encodeService;

    @Override
    @Transactional
    public ResponseDto<JwtTokenPairResponseDto> issueTokens(String memberId) {
        String accessToken = accessTokenService.generateToken(memberId);
        String refreshToken = refreshTokenService.generateToken();
        String token_id = UUID.randomUUID().toString();
        Token token = Token.builder()
                .token_id(token_id)
                .access_token(accessToken)
                .refresh_token(refreshToken)
                .build();
        addAccessExpiry(token);
        addRefreshExpiry(token);
        token.setAccess_token(encodeService.encode(accessToken));
        token.setRefresh_token(encodeService.encode(refreshToken));
        JwtTokenPairResponseDto responseDto = JwtTokenPairResponseDto.set(accessToken, refreshToken);
        try{
            if (!tokenRepository.saveToken(token)) {
                throw new RuntimeException("Token保存失敗");
            }
            if (!memberTokenRepository.saveMemberToken(memberId, token_id)) {
                throw new RuntimeException("Token保存失敗");
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseDto.fail("Token保存失敗");
        }
        return ResponseDto.success("Token生成成功", responseDto);
    }

    @Override
    public String refreshAccessToken(RefreshAccessTokenRequestDto requestDto) {
        return "";
    }

    private void addAccessExpiry(Token token) {
        Claims claims = accessTokenService.getClaims(token.getAccess_token());
        token.setAccess_token_expiry(claims.getExpiration());
    }
    private void addRefreshExpiry(Token token) {
        Claims claims = refreshTokenService.getClaims(token.getRefresh_token());
        token.setRefresh_token_expiry(claims.getExpiration());
    }
}
