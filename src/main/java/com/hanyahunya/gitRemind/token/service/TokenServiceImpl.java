package com.hanyahunya.gitRemind.token.service;

import com.hanyahunya.gitRemind.token.dto.JwtTokenPairResponseDto;
import com.hanyahunya.gitRemind.token.dto.RefreshAccessTokenRequestDto;
import com.hanyahunya.gitRemind.token.entity.Token;
import com.hanyahunya.gitRemind.token.repository.MemberTokenRepository;
import com.hanyahunya.gitRemind.token.repository.TokenRepository;
import com.hanyahunya.gitRemind.util.ResponseDto;
import com.hanyahunya.gitRemind.util.TokenCookieHeaderGenerator;
import com.hanyahunya.gitRemind.util.service.EncodeService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final TokenRepository tokenRepository;
    private final MemberTokenRepository memberTokenRepository;
    private final AccessTokenService accessTokenService;
    private final RefreshTokenService refreshTokenService;
    private final EncodeService encodeService;
    private final TokenCookieHeaderGenerator tokenCookieHeaderGenerator;

    @Override
    @Transactional
    public ResponseDto<JwtTokenPairResponseDto> issueTokens(String memberId) {
        String accessToken = accessTokenService.generateToken(memberId);
        String token_id = UUID.randomUUID().toString();
        String refreshToken = refreshTokenService.generateToken(token_id);
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


    // todo (04.28) トークンの盗難が疑われる場合、[セキュリティ上の問題により、このアカウントはログアウトされました]のようなメールをユーザーに送る
    @Override
    @Transactional
    public HttpHeaders refreshAccessToken(String oldAccessToken, String refreshToken) {
        try {
            if (!refreshTokenService.validateToken(refreshToken)) {
                return null;
            }
            String tokenId = refreshTokenService.getClaims(refreshToken).get("token_id", String.class);
            HttpHeaders headers = new HttpHeaders();
            Optional<Token> optionalToken = tokenRepository.findByTokenId(tokenId);
            if (optionalToken.isEmpty()) {
                deleteToken(headers);
                return headers;
            }
            // アクセストークンの有効期限が切れてないのに更新リクエスト
            Token token = optionalToken.get();
            if (!accessTokenService.isTokenExpired(token.getAccess_token_expiry())) {
                tokenRepository.deleteByTokenId(token.getToken_id());
                deleteToken(headers);
                return headers;
            }
            // 当サーバーが以前発行したアクセストークンと一致しない
            if (!encodeService.matches(oldAccessToken, token.getAccess_token()) || !encodeService.matches(refreshToken, token.getRefresh_token())) {
                tokenRepository.deleteByTokenId(token.getToken_id());
                deleteToken(headers);
                return headers;
            }
            String memberId = memberTokenRepository.findMemberIdByTokenId(token.getToken_id());
            String newAccessToken = accessTokenService.generateToken(memberId);

            Token updatedToken = Token.builder()
                    .token_id(token.getToken_id())
                    .access_token(newAccessToken)
                    .build();
            addAccessExpiry(updatedToken);
            updatedToken.setAccess_token(encodeService.encode(newAccessToken));
            if (!tokenRepository.updateToken(updatedToken)) {
                return null;
            }

            String deleteAccessToken = tokenCookieHeaderGenerator.deleteAccessToken();
            String accessTokenHeader = tokenCookieHeaderGenerator.buildByAccessToken(newAccessToken);
            headers.add(HttpHeaders.SET_COOKIE, deleteAccessToken);
            headers.add(HttpHeaders.SET_COOKIE, accessTokenHeader);
            return headers;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error("{}-{}: {}", this.getClass().getSimpleName(), e.getClass().getSimpleName(), e.getMessage());
            return null;
        }
    }

    private void deleteToken(HttpHeaders headers) {
        String deleteAccessToken = tokenCookieHeaderGenerator.deleteAccessToken();
        String deleteRefreshToken = tokenCookieHeaderGenerator.deleteRefreshToken();
        headers.add(HttpHeaders.SET_COOKIE, deleteAccessToken);
        headers.add(HttpHeaders.SET_COOKIE, deleteRefreshToken);
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
