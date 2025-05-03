package com.hanyahunya.gitRemind.token.service;

import com.hanyahunya.gitRemind.contribution.entity.Contribution;
import com.hanyahunya.gitRemind.contribution.repository.ContributionRepository;
import com.hanyahunya.gitRemind.token.dto.JwtTokenPairResponseDto;
import com.hanyahunya.gitRemind.token.entity.Token;
import com.hanyahunya.gitRemind.token.repository.MemberTokenRepository;
import com.hanyahunya.gitRemind.token.repository.TokenRepository;
import com.hanyahunya.gitRemind.util.ResponseDto;
import com.hanyahunya.gitRemind.util.cookieHeader.SetResultDto;
import com.hanyahunya.gitRemind.util.service.EncodeService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final TokenRepository tokenRepository;
    private final MemberTokenRepository memberTokenRepository;
    private final ContributionRepository contributionRepository;
    private final AccessTokenService accessTokenService;
    private final RefreshTokenService refreshTokenService;
    private final EncodeService encodeService;
    private final SecurityAlertEmailService securityAlertEmailService;

    @Override
    @Transactional
    public ResponseDto<JwtTokenPairResponseDto> issueTokens(String memberId) {
        String tokenId = UUID.randomUUID().toString();
        String accessToken = accessTokenService.generateToken(memberId, tokenId);
        String refreshToken = refreshTokenService.generateToken(tokenId);
        Token token = Token.builder()
                .tokenId(tokenId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        addAccessExpiry(token);
        addRefreshExpiry(token);
        token.setAccessToken(encodeService.encode(accessToken));
        token.setRefreshToken(encodeService.encode(refreshToken));
        JwtTokenPairResponseDto responseDto = JwtTokenPairResponseDto.set(accessToken, refreshToken);
        try{
            if (!tokenRepository.saveToken(token)) {
                throw new RuntimeException("Token保存失敗");
            }
            if (!memberTokenRepository.saveMemberToken(memberId, tokenId)) {
                throw new RuntimeException("Token保存失敗");
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseDto.fail("Token保存失敗");
        }
        return ResponseDto.success("Token生成成功", responseDto);
    }


    @Override
    @Transactional
    public SetResultDto refreshAccessToken(String oldAccessToken, String refreshToken) {
        try {
            SetResultDto setResultDto = SetResultDto.builder().build();
            Claims refreshTokenClaims;
            try {
                refreshTokenClaims = refreshTokenService.getClaims(refreshToken);
            } catch (ExpiredJwtException e) {
                String tokenId = e.getClaims().get("token_id", String.class);
                tokenRepository.deleteByTokenId(tokenId);
                deleteToken(setResultDto);
                return setResultDto;
            } catch (Exception e) {
                deleteToken(setResultDto);
                return setResultDto;
            }
            String tokenId = refreshTokenClaims.get("token_id", String.class);
//            HttpHeaders headers = new HttpHeaders();
            Optional<Token> optionalToken = tokenRepository.findByTokenId(tokenId);
            if (optionalToken.isEmpty()) {
                deleteToken(setResultDto);
                return setResultDto;
            }
            // アクセストークンの有効期限が切れてないのに更新リクエスト
            Token token = optionalToken.get();
            String memberId = memberTokenRepository.findMemberIdByTokenId(token.getTokenId());
            if (!accessTokenService.isTokenExpired(token.getAccessTokenExpiry())) {
                sendHijackAlert(memberId);
                tokenRepository.deleteByTokenId(token.getTokenId());
                deleteToken(setResultDto);
                return setResultDto;
            }
            // 当サーバーが以前発行したアクセストークンと一致しない
            if (!encodeService.matches(oldAccessToken, token.getAccessToken()) || !encodeService.matches(refreshToken, token.getRefreshToken())) {
                sendHijackAlert(memberId);
                tokenRepository.deleteByTokenId(token.getTokenId());
                deleteToken(setResultDto);
                return setResultDto;
            }
            String newAccessToken = accessTokenService.generateToken(memberId, token.getTokenId());
            Token updatedToken = Token.builder()
                    .tokenId(token.getTokenId())
                    .accessToken(newAccessToken)
                    .build();
            addAccessExpiry(updatedToken);
            updatedToken.setAccessToken(encodeService.encode(newAccessToken));

            boolean isRefreshTokenShouldRenew = isRefreshTokenExpiringSoon(refreshToken);
            String newRefreshToken = "";
            if (isRefreshTokenShouldRenew) {
                newRefreshToken = refreshTokenService.generateToken(tokenId);
                updatedToken.setRefreshToken(newRefreshToken);
                addRefreshExpiry(updatedToken);
                updatedToken.setRefreshToken(encodeService.encode(newRefreshToken));
            }

            if (!tokenRepository.updateToken(updatedToken)) {
                setResultDto.setSuccess(false);
                return setResultDto;
            }

            setResultDto.setSuccess(true);
            setResultDto.setAccessToken(newAccessToken);

            if (isRefreshTokenShouldRenew) {
                setResultDto.setRefreshToken(newRefreshToken);
            }

            return setResultDto;

        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error("{}-{}: {}", this.getClass().getSimpleName(), e.getClass().getSimpleName(), e.getMessage());
            return null;
        }
    }

    @Override
    public ResponseDto<Void> deleteTokenAtAllDevice(String memberId) {
        if (memberTokenRepository.deleteAllByMemberId(memberId)) {
            return ResponseDto.success("すべてのデヴァイスからログアウト成功");
        } else {
            return ResponseDto.fail("すべてのデヴァイスからログアウト失敗");
        }
    }

    @Override
    public SetResultDto deleteTokenCurrentDevice(String tokenId) {
        tokenRepository.deleteByTokenId(tokenId);
        return SetResultDto.builder().success(true).deleteAccessToken(true).deleteRefreshToken(true).build();
    }

    @Override
    public void deleteTokenOtherDevice(String memberId, String tokenId) {
        memberTokenRepository.deleteAllByMemberIdAndTokenIdNot(memberId, tokenId);
    }

    private boolean isRefreshTokenExpiringSoon(String refreshToken) {
        final long THREE_DAYS_MILLIS = 3L * 24 * 60 * 60 * 1000;
        Date now = new Date();
        Date expirationDate = refreshTokenService.getClaims(refreshToken).getExpiration();
        return (expirationDate.getTime() - now.getTime()) < THREE_DAYS_MILLIS;
    }

    private void sendHijackAlert(String memberId) {
        Optional<Contribution> optionalContribution = contributionRepository.getContributionByMemberId(memberId);
        if (optionalContribution.isPresent()) {
            String email = optionalContribution.get().getEmail();
            String gitUsername = optionalContribution.get().getGitUsername();
//            System.out.println(email + gitUsername);
            securityAlertEmailService.sendCookieHijackingAlert(email, gitUsername);
        }
    }

    private void deleteToken(SetResultDto setResultDto) {
        setResultDto.setSuccess(false);
        setResultDto.setDeleteAccessToken(true);
        setResultDto.setDeleteRefreshToken(true);
    }

    private void addAccessExpiry(Token token) {
        Claims claims = accessTokenService.getClaims(token.getAccessToken());
        token.setAccessTokenExpiry(claims.getExpiration());
    }
    private void addRefreshExpiry(Token token) {
        Claims claims = refreshTokenService.getClaims(token.getRefreshToken());
        token.setRefreshTokenExpiry(claims.getExpiration());
    }
}
