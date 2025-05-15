package com.hanyahunya.gitRemind.integration.token.service;

import com.hanyahunya.gitRemind.member.entity.Member;
import com.hanyahunya.gitRemind.member.repository.MemberRepository;
import com.hanyahunya.gitRemind.token.dto.JwtTokenPairResponseDto;
import com.hanyahunya.gitRemind.token.entity.Token;
import com.hanyahunya.gitRemind.token.repository.MemberTokenRepository;
import com.hanyahunya.gitRemind.token.repository.TokenRepository;
import com.hanyahunya.gitRemind.token.service.*;
import com.hanyahunya.gitRemind.util.ResponseDto;
import com.hanyahunya.gitRemind.util.cookieHeader.SetResultDto;
import com.hanyahunya.gitRemind.util.service.EncodeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TokenServiceImplIT {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private MemberTokenRepository memberTokenRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private EncodeService encodeService;

    @Value("${jwt.accessToken.secret}")
    private String accessJwtKey;
    @Value("${jwt.refreshToken.secret}")
    private String refreshJwtKey;

    private final long defaultAccessTokenExpireTime = 900000;
    private final long defaultRefreshTokenExpireTime = 604800000;
    private final JwtAccessTokenService customAccessTokenService = new JwtAccessTokenService();
    private final JwtRefreshTokenService customRefreshTokenService = new JwtRefreshTokenService();

    @Autowired
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    private final String defaultMemberId = UUID.randomUUID().toString();
    private final String defaultTokenId = UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {
        jdbcTemplate = new JdbcTemplate(dataSource);
        Member defaultMember = Member.builder()
                .memberId(defaultMemberId)
                .loginId("id")
                .password(encodeService.encode("password"))
                .email("gkalsaka0103@gmail.com")
                .country("KR")
                .build();
        // add default member
        memberRepository.saveMember(defaultMember);

        customAccessTokenService.setJwtKey(accessJwtKey);
        customRefreshTokenService.setJwtKey(refreshJwtKey);
        customAccessTokenService.setExpirationTime(defaultAccessTokenExpireTime);
        customRefreshTokenService.setExpirationTime(defaultRefreshTokenExpireTime);
        customAccessTokenService.init();
        customRefreshTokenService.init();
    }

    @Test
    void issueTokens() {
        // when
        ResponseDto<JwtTokenPairResponseDto> response = tokenService.issueTokens(defaultMemberId);

        // then
        assertTrue(response.isSuccess());

        String sql = "SELECT token_id FROM member_token WHERE member_id = ?";
        String dbTokenId = jdbcTemplate.queryForObject(sql, String.class, defaultMemberId);
        assertNotNull(dbTokenId);

        String sql1 = "SELECT access_token, refresh_token FROM token WHERE token_id = ?";
        Map<String, Object> dbToken = jdbcTemplate.queryForMap(sql1, dbTokenId);

        assertTrue(encodeService.matches(response.getData().getAccessToken(), dbToken.get("access_token").toString()));
        assertTrue(encodeService.matches(response.getData().getRefreshToken(), dbToken.get("refresh_token").toString()));
    }
    // for <refreshAccessToken>
    private void saveTokenToDb(String tokenId, String accessToken, String refreshToken) {
        Token token = Token.builder()
                .tokenId(tokenId)
                .accessToken(encodeService.encode(accessToken))
                .refreshToken(encodeService.encode(refreshToken))
                .accessTokenExpiry(customAccessTokenService.getExpirationDate(accessToken)) // expiredToken
                .refreshTokenExpiry(customRefreshTokenService.getExpirationDate(refreshToken))
                .build();
        tokenRepository.saveToken(token);
        memberTokenRepository.saveMemberToken(defaultMemberId, tokenId);
    }
    @Test
    void refreshAccessToken() {
        // given
        customAccessTokenService.setExpirationTime(-3000);
        String oldAccessToken = customAccessTokenService.generateToken(defaultMemberId, defaultTokenId);
        String refreshToken = customRefreshTokenService.generateToken(defaultTokenId);

        saveTokenToDb(defaultTokenId, oldAccessToken, refreshToken);

        // when
        SetResultDto response = tokenService.refreshAccessToken(oldAccessToken, refreshToken);

        // then
        String responseAccessToken = response.getAccessToken();
        assertTrue(response.isSuccess());
        assertNotNull(responseAccessToken);
        assertNotEquals(oldAccessToken, responseAccessToken);

        String sql = "SELECT access_token, access_token_expiry FROM token WHERE token_id = ?";
        Map<String, Object> dbToken = jdbcTemplate.queryForMap(sql, defaultTokenId);
        assertTrue(encodeService.matches(responseAccessToken, dbToken.get("access_token").toString()));
        assertEquals(customAccessTokenService.getExpirationDate(responseAccessToken), dbToken.get("access_token_expiry"));
    }
    @Test
    @DisplayName("<refreshAccessToken>リフレッシュトークンの有効期限が3日未満の場合")
    void refreshAccessTokenWhenRefreshTokenShouldRefresh() {
        // given
        customAccessTokenService.setExpirationTime(-3000);
        String oldAccessToken = customAccessTokenService.generateToken(defaultMemberId, defaultTokenId);
        customRefreshTokenService.setExpirationTime(1000 * 60 * 60 * 24 * 2); // +2days
        String oldRefreshToken = customRefreshTokenService.generateToken(defaultTokenId);

        saveTokenToDb(defaultTokenId, oldAccessToken, oldRefreshToken);

        // when
        SetResultDto response = tokenService.refreshAccessToken(oldAccessToken, oldRefreshToken);

        // then
        String responseAccessToken = response.getAccessToken();
        String responseRefreshToken = response.getRefreshToken();
        assertTrue(response.isSuccess());
        assertNotNull(responseAccessToken);
        assertNotEquals(oldAccessToken, responseAccessToken);
        assertNotNull(response.getRefreshToken());
        assertNotEquals(oldRefreshToken, response.getRefreshToken());

        String sql = "SELECT access_token, refresh_token, access_token_expiry, refresh_token_expiry FROM token WHERE token_id = ?";
        Map<String, Object> dbToken = jdbcTemplate.queryForMap(sql, defaultTokenId);
        assertTrue(encodeService.matches(responseAccessToken, dbToken.get("access_token").toString()));
        assertTrue(encodeService.matches(response.getRefreshToken(), dbToken.get("refresh_token").toString()));
        assertEquals(customAccessTokenService.getExpirationDate(responseAccessToken), dbToken.get("access_token_expiry"));
        assertEquals(customRefreshTokenService.getExpirationDate(responseRefreshToken), dbToken.get("refresh_token_expiry"));
    }
    @Test
    @DisplayName("<refreshAccessToken>リフレッシュトークン有効期限切れの場合")
    void refreshAccessTokenFailsWhenRefreshTokenExpired() {
        // given
        String accessToken = customAccessTokenService.generateToken(defaultMemberId, defaultTokenId);
        customRefreshTokenService.setExpirationTime(-3000);
        String expiredRefreshToken = customRefreshTokenService.generateToken(defaultTokenId);

        saveTokenToDb(defaultTokenId, accessToken, expiredRefreshToken);

        // when
        SetResultDto response = tokenService.refreshAccessToken(accessToken, expiredRefreshToken);

        // then
        assertFalse(response.isSuccess());
        assertTrue(response.isDeleteAccessToken());
        assertTrue(response.isDeleteRefreshToken());

        String tokenSql = "SELECT COUNT(*) FROM token WHERE token_id = ?";
        int tokenRows = jdbcTemplate.queryForObject(tokenSql, Integer.class, defaultTokenId);
        assertEquals(0, tokenRows);
        String memberTokenSql = "SELECT COUNT(*) FROM member_token WHERE token_id = ?";
        int memberTokenRows = jdbcTemplate.queryForObject(memberTokenSql, Integer.class, defaultTokenId);
        assertEquals(0, memberTokenRows);
    }
    @Test
    @DisplayName("<refreshAccessToken>DB上のアクセストークンの有効期限が切れてない状態で更新を試みた場合")
    void refreshAccessTokenFailsWhenAccessTokenNotExpired() {
        // given
        String accessToken = customAccessTokenService.generateToken(defaultMemberId, defaultTokenId);
        String refreshToken = customRefreshTokenService.generateToken(defaultTokenId);
        saveTokenToDb(defaultTokenId, accessToken, refreshToken);

        // when
        SetResultDto response = tokenService.refreshAccessToken(accessToken, refreshToken);

        // then
        assertFalse(response.isSuccess());
        assertTrue(response.isDeleteAccessToken());
        assertTrue(response.isDeleteRefreshToken());

        String tokenSql = "SELECT COUNT(*) FROM token WHERE token_id = ?";
        int tokenRows = jdbcTemplate.queryForObject(tokenSql, Integer.class, defaultTokenId);
        assertEquals(0, tokenRows);

        String memberTokenSql = "SELECT COUNT(*) FROM member_token WHERE token_id = ?";
        int memberTokenRows = jdbcTemplate.queryForObject(memberTokenSql, Integer.class, defaultTokenId);
        assertEquals(0, memberTokenRows);
    }
    @Test
    @DisplayName("<refreshAccessToken>当サーバーが以前発行したアクセストークンと一致しない場合")
    void refreshAccessTokenFailsWhenAccessTokenInvalid() {
        customAccessTokenService.setExpirationTime(-3000);
        String accessToken = customAccessTokenService.generateToken(defaultMemberId, defaultTokenId);
        String refreshToken = customRefreshTokenService.generateToken(defaultTokenId);
        saveTokenToDb(defaultTokenId, accessToken, refreshToken);

        customAccessTokenService.setExpirationTime(-2000);
        String fakeAccessToken = customAccessTokenService.generateToken(defaultMemberId, defaultTokenId);

        // when
        SetResultDto response = tokenService.refreshAccessToken(fakeAccessToken, refreshToken);

        // then
        assertFalse(response.isSuccess());
        assertTrue(response.isDeleteAccessToken());
        assertTrue(response.isDeleteRefreshToken());

        String tokenSql = "SELECT COUNT(*) FROM token WHERE token_id = ?";
        int tokenRows = jdbcTemplate.queryForObject(tokenSql, Integer.class, defaultTokenId);
        assertEquals(0, tokenRows);

        String memberTokenSql = "SELECT COUNT(*) FROM member_token WHERE token_id = ?";
        int memberTokenRows = jdbcTemplate.queryForObject(memberTokenSql, Integer.class, defaultTokenId);
        assertEquals(0, memberTokenRows);
    }

    @Test
    void deleteTokenAtAllDevice() {
        // given?
        String accessToken = customAccessTokenService.generateToken(defaultMemberId, defaultTokenId);
        String refreshToken = customRefreshTokenService.generateToken(defaultTokenId);
        saveTokenToDb(defaultTokenId, accessToken, refreshToken);

        String tokenId1 = UUID.randomUUID().toString();
        String accessToken1 = customAccessTokenService.generateToken(defaultMemberId, tokenId1);
        String refreshToken1 = customRefreshTokenService.generateToken(tokenId1);
        saveTokenToDb(tokenId1, accessToken1, refreshToken1);

        // when
        ResponseDto<Void> response = tokenService.deleteTokenAtAllDevice(defaultMemberId);

        // then
        assertTrue(response.isSuccess());

        String tokenSql = "SELECT COUNT(*) FROM token WHERE token_id = ?";
        int tokenRows = jdbcTemplate.queryForObject(tokenSql, Integer.class, defaultTokenId);
        assertEquals(0, tokenRows);

        String memberTokenSql = "SELECT COUNT(*) FROM member_token WHERE token_id = ?";
        int memberTokenRows = jdbcTemplate.queryForObject(memberTokenSql, Integer.class, defaultTokenId);
        assertEquals(0, memberTokenRows);
    }

    @Test
    void deleteTokenCurrentDevice() {
        // given
        String currentAccessToken = customAccessTokenService.generateToken(defaultMemberId, defaultTokenId);
        String refreshToken = customRefreshTokenService.generateToken(defaultTokenId);
        saveTokenToDb(defaultTokenId, currentAccessToken, refreshToken);

        String tokenId1 = UUID.randomUUID().toString();
        String otherAccessToken = customAccessTokenService.generateToken(defaultMemberId, tokenId1);
        String otherRefreshToken = customRefreshTokenService.generateToken(tokenId1);
        saveTokenToDb(tokenId1, otherAccessToken, otherRefreshToken);

        // when
        SetResultDto response = tokenService.deleteTokenCurrentDevice(defaultTokenId);

        // then
        assertTrue(response.isSuccess());
        assertTrue(response.isDeleteAccessToken());
        assertTrue(response.isDeleteRefreshToken());

        String tokenSql = "SELECT COUNT(*) FROM token WHERE token_id = ?";
        int tokenRows = jdbcTemplate.queryForObject(tokenSql, Integer.class, defaultTokenId);
        assertEquals(0, tokenRows);

        String memberTokenSql = "SELECT COUNT(*) FROM member_token WHERE member_id = ?";
        int memberTokenRows = jdbcTemplate.queryForObject(memberTokenSql, Integer.class, defaultMemberId);
        assertEquals(1, memberTokenRows);

    }

    @Test
    void deleteTokenOtherDevice() {
        // given
        String currentAccessToken = customAccessTokenService.generateToken(defaultMemberId, defaultTokenId);
        String refreshToken = customRefreshTokenService.generateToken(defaultTokenId);
        saveTokenToDb(defaultTokenId, currentAccessToken, refreshToken);

        String tokenId1 = UUID.randomUUID().toString();
        String otherAccessToken = customAccessTokenService.generateToken(defaultMemberId, tokenId1);
        String otherRefreshToken = customRefreshTokenService.generateToken(tokenId1);
        saveTokenToDb(tokenId1, otherAccessToken, otherRefreshToken);

        // when
        tokenService.deleteTokenOtherDevice(defaultMemberId, defaultTokenId);

        // then
        String tokenSql = "SELECT COUNT(*) FROM token WHERE token_id = ?";
        int tokenRows = jdbcTemplate.queryForObject(tokenSql, Integer.class, defaultTokenId);
        assertEquals(1, tokenRows);

        String memberTokenSql = "SELECT COUNT(*) FROM member_token WHERE member_id = ?";
        int memberTokenRows = jdbcTemplate.queryForObject(memberTokenSql, Integer.class, defaultMemberId);
        assertEquals(1, memberTokenRows);
    }
}