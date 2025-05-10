package com.hanyahunya.gitRemind.unit.member.service;

import com.hanyahunya.gitRemind.member.dto.*;
import com.hanyahunya.gitRemind.member.entity.Member;
import com.hanyahunya.gitRemind.member.repository.MemberRepository;
import com.hanyahunya.gitRemind.member.service.MemberService;
import com.hanyahunya.gitRemind.member.service.MemberServiceImpl;
import com.hanyahunya.gitRemind.token.dto.JwtTokenPairResponseDto;
import com.hanyahunya.gitRemind.token.service.TokenService;
import com.hanyahunya.gitRemind.util.ResponseDto;
import com.hanyahunya.gitRemind.util.cookieHeader.SetResultDto;
import com.hanyahunya.gitRemind.util.service.EncodeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MemberServiceImplTest {
    private MemberRepository memberRepository;
    private TokenService tokenService;
    private EncodeService encodeService;
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        memberRepository = Mockito.mock(MemberRepository.class);
        tokenService = Mockito.mock(TokenService.class);
        encodeService = Mockito.mock(EncodeService.class);

        memberService = new MemberServiceImpl(memberRepository, tokenService, encodeService);
    }

    @Test
    void join() {
        // given
        JoinRequestDto requestDto = new JoinRequestDto();
        requestDto.setLoginId("test");
        requestDto.setPassword("rawPassword");
        requestDto.setEmail("test@example.com");
        requestDto.setCountry("KR");

        // rawPasswordっていう文字列を受けたら、encodedPasswordを返す
        when(encodeService.encode("rawPassword")).thenReturn("encodedPassword");

        // どのMemberオブジェクトが入っても、trueを返す
        when(memberRepository.saveMember(any(Member.class))).thenReturn(true);

        // when
        ResponseDto<Void> response = memberService.join(requestDto);

        // then
        assertTrue(response.isSuccess()); //ロジックが成功したら、response.isSuccess()を返すのか
        assertEquals("会員登録成功", response.getMessage());

        //　encodeServiceのencodeメソッドに入った文字列がrawPasswordなのか。このメソッドが一回呼ばれたか
        verify(encodeService).encode("rawPassword");
        //　memberRepositoryのsaveMemberメソッドに入ったMemberオブジェクトが正しく入ったのか
        verify(memberRepository).saveMember(argThat(member ->
                        member.getMemberId().length() == 36 &&
                        member.getLoginId().equals("test") &&
                        member.getPassword().equals("encodedPassword") &&
                        member.getEmail().equals("test@example.com") &&
                        member.getCountry().equals("KR")
        ));
    }
    @Test
    @DisplayName("<join>リポジトリの問題からの失敗")
    void joinFailsWhenRepositoryReturnsFalse() {
        // given
        JoinRequestDto requestDto = new JoinRequestDto();
        requestDto.setLoginId("test");
        requestDto.setPassword("rawPassword");
        requestDto.setEmail("test@example.com");
        requestDto.setCountry("KR");

        when(encodeService.encode("rawPassword")).thenReturn("encodedPassword");

        when(memberRepository.saveMember(any(Member.class))).thenReturn(false);

        // when
        ResponseDto<Void> response = memberService.join(requestDto);

        // then
        assertFalse(response.isSuccess());
        assertEquals("会員登録失敗", response.getMessage());

        verify(encodeService).encode("rawPassword");
        verify(memberRepository).saveMember(any(Member.class));
    }

    @Test
    void login() {
        // given
        LoginRequestDto requestDto = new LoginRequestDto();
        requestDto.setLoginId("test");
        requestDto.setPassword("rawPassword");

        String uuid = UUID.randomUUID().toString();
        Member dbMember = Member.builder().memberId(uuid).password("encodedPassword").build();
        when(memberRepository.findMemberByLoginId(argThat(member ->
                member.getLoginId().equals("test"))))
                .thenReturn(Optional.of(dbMember));
        when(encodeService.matches("rawPassword", "encodedPassword")).thenReturn(true);

        String accessToken = "mockAccessToken";
        String refreshToken = "mockRefreshToken";
        JwtTokenPairResponseDto tokenPair = JwtTokenPairResponseDto.set(accessToken, refreshToken);
        ResponseDto<JwtTokenPairResponseDto> mockResponse = ResponseDto.success("Token生成成功", tokenPair);
        when(tokenService.issueTokens(uuid)).thenReturn(mockResponse);

        // when
        SetResultDto response = memberService.login(requestDto);

        //then
        assertTrue(response.isSuccess());
        assertEquals(accessToken, response.getAccessToken());
        assertEquals(refreshToken, response.getRefreshToken());

        verify(memberRepository).findMemberByLoginId(argThat(member -> member.getLoginId().equals("test")));
        verify(encodeService).matches("rawPassword", "encodedPassword");
        verify(tokenService).issueTokens(uuid);
    }
    @Test
    @DisplayName("<login>Idに該当するユーザーがない場合の失敗")
    void loginFailsWhenMemberNotFound() {
        // given
        LoginRequestDto requestDto = new LoginRequestDto();
        requestDto.setLoginId("test");
        requestDto.setPassword("rawPassword");

        when(memberRepository.findMemberByLoginId(any())).thenReturn(Optional.empty());

        // when
        SetResultDto response = memberService.login(requestDto);

        // then
        assertFalse(response.isSuccess());
        assertNull(response.getAccessToken());
        assertNull(response.getRefreshToken());
    }
    @Test
    @DisplayName("<login>パスワード不一致からの失敗")
    void loginFailsWhenPasswordIncorrect() {
        // given
        LoginRequestDto requestDto = new LoginRequestDto();
        requestDto.setLoginId("test");
        requestDto.setPassword("wrongPassword");

        Member dbMember = Member.builder().memberId("some-id").password("encodedPassword").build();
        when(memberRepository.findMemberByLoginId(any())).thenReturn(Optional.of(dbMember));
        when(encodeService.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        // when
        SetResultDto response = memberService.login(requestDto);

        // then
        assertFalse(response.isSuccess());
        assertNull(response.getAccessToken());
        assertNull(response.getRefreshToken());
    }

    @Test
    void getInfo() {
        // given
        String uuid = UUID.randomUUID().toString();
        Member dbMember = Member.builder().password("encodedPassword").email("test@example.com").country("KR").build();
        when(memberRepository.findMemberByMemberId(eq(uuid))).thenReturn(Optional.of(dbMember));

        // when
        ResponseDto<MemberInfoResponseDto> response = memberService.getInfo(uuid);

        // then
        assertTrue(response.isSuccess());
        assertEquals("ユーザー情報ロード成功", response.getMessage());
        assertEquals("test@example.com", response.getData().getEmail());
        assertEquals("KR", response.getData().getCountry());

        verify(memberRepository).findMemberByMemberId(eq(uuid));
    }
    @Test
    @DisplayName("<getInfo>Idに該当するユーザーがない場合の失敗")
    void getInfoFailsWhenMemberNotFound() {
        // given
        String uuid = UUID.randomUUID().toString();
        when(memberRepository.findMemberByMemberId(eq(uuid)))
                .thenReturn(Optional.empty()); // 회원 없음

        // when
        ResponseDto<MemberInfoResponseDto> response = memberService.getInfo(uuid);

        // then
        assertFalse(response.isSuccess());
        assertEquals("ユーザー情報ロード失敗", response.getMessage());
        assertNull(response.getData());

        verify(memberRepository).findMemberByMemberId(eq(uuid));
    }

    @Test
    void deleteMember() {
        // given
        String uuid = UUID.randomUUID().toString();
        DeleteMemberRequestDto requestDto = new DeleteMemberRequestDto();
        requestDto.setMemberId(uuid);
        requestDto.setLoginId("test");
        requestDto.setPassword("rawPassword");

        Member dbMember = Member.builder().loginId("test").password("encodedPassword").email("test@example.com").country("KR").build();
        when(memberRepository.findMemberByMemberId(eq(uuid))).thenReturn(Optional.of(dbMember));
        when(encodeService.matches("rawPassword", "encodedPassword")).thenReturn(true);
        when(memberRepository.deleteMember(argThat(member ->
                        member.getMemberId().equals(uuid) &&
                        member.getLoginId().equals("test") &&
                        member.getPassword().equals("rawPassword")
                        ))
        ).thenReturn(true);
        when(tokenService.deleteTokenAtAllDevice(eq(uuid))).thenReturn(ResponseDto.success("すべてのデヴァイスからログアウト成功"));

        // when
        SetResultDto response = memberService.deleteMember(requestDto);

        //then
        assertTrue(response.isSuccess());
        assertTrue(response.isDeleteAccessToken());
        assertTrue(response.isDeleteRefreshToken());

        verify(memberRepository).findMemberByMemberId(eq(uuid));
        verify(encodeService).matches("rawPassword", "encodedPassword");
        verify(tokenService).deleteTokenAtAllDevice(eq(uuid));
    }
    @Test
    @DisplayName("<deleteMember>パスワード不一致からの失敗")
    void deleteMemberFailsWhenPasswordIncorrect() {
        // given
        String uuid = UUID.randomUUID().toString();
        DeleteMemberRequestDto requestDto = new DeleteMemberRequestDto();
        requestDto.setMemberId(uuid);
        requestDto.setLoginId("test");
        requestDto.setPassword("wrongPassword");

        Member dbMember = Member.builder().loginId("test").password("encodedPassword").build();
        when(memberRepository.findMemberByMemberId(eq(uuid))).thenReturn(Optional.of(dbMember));
        when(encodeService.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        // when
        SetResultDto response = memberService.deleteMember(requestDto);

        // then
        assertFalse(response.isSuccess());
        verify(memberRepository).findMemberByMemberId(eq(uuid));
        verify(encodeService).matches("wrongPassword", "encodedPassword");
    }


    @Test
    void updateMember() {
        // given
        String uuid = UUID.randomUUID().toString();
        UpdateMemberRequestDto requestDto = new UpdateMemberRequestDto();
        requestDto.setMemberId(uuid);
        requestDto.setLoginId("test");
        requestDto.setPassword("rawPassword");
        requestDto.setEmail("newEmail@example.com");
        requestDto.setCountry("KR");

        Member dbMember = Member.builder().loginId("test").password("encodedPassword").email("test@example.com").build();
        when(memberRepository.findMemberByMemberId(eq(uuid))).thenReturn(Optional.of(dbMember));
        when(encodeService.matches("rawPassword", "encodedPassword")).thenReturn(true);
        when(memberRepository.updateMember(argThat(member ->
                        member.getMemberId().equals(uuid) &&
                        member.getEmail().equals("newEmail@example.com") &&
                        member.getCountry().equals("KR")
                ))
        ).thenReturn(true);

        // when
        ResponseDto<Void> response = memberService.updateMember(requestDto);

        // then
        assertTrue(response.isSuccess());
        assertEquals("ユーザー情報更新成功", response.getMessage());

        verify(memberRepository).findMemberByMemberId(eq(uuid));
        verify(encodeService).matches("rawPassword", "encodedPassword");
        verify(memberRepository).updateMember(argThat(member ->
                member.getMemberId().equals(uuid) &&
                member.getEmail().equals("newEmail@example.com") &&
                member.getCountry().equals("KR")
        ));
    }
}