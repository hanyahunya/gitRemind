package com.hanyahunya.gitRemind.unit.member.service;

import com.hanyahunya.gitRemind.member.dto.ChangePwRequestDto;
import com.hanyahunya.gitRemind.member.dto.ResetPwRequestDto;
import com.hanyahunya.gitRemind.member.entity.Member;
import com.hanyahunya.gitRemind.member.repository.MemberRepository;
import com.hanyahunya.gitRemind.member.service.PasswordService;
import com.hanyahunya.gitRemind.member.service.PasswordServiceImpl;
import com.hanyahunya.gitRemind.token.service.TokenService;
import com.hanyahunya.gitRemind.util.ResponseDto;
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

class PasswordServiceImplTest {
    private PasswordService passwordService;
    private MemberRepository memberRepository;
    private EncodeService encodeService;
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        memberRepository = Mockito.mock(MemberRepository.class);
        encodeService = Mockito.mock(EncodeService.class);
        tokenService = Mockito.mock(TokenService.class);

        passwordService = new PasswordServiceImpl(memberRepository, encodeService, tokenService);
    }

    @Test
    void forgotPassword() {
        // given
        ResetPwRequestDto request = new ResetPwRequestDto();
        request.setEmail("test@example.com");
        request.setNewPassword("newPassword");

        when(encodeService.encode("newPassword")).thenReturn("encodedNewPassword");
        String uuid = UUID.randomUUID().toString();
        Member dbMember = Member.builder().memberId(uuid).country("KR").build();
        when(memberRepository.findMemberByEmail("test@example.com")).thenReturn(Optional.of(dbMember));
        when(memberRepository.updateMember(argThat(member ->
                        member.getMemberId().equals(uuid) &&
                        member.getPassword().equals("encodedNewPassword")))
        ).thenReturn(true);
        when(tokenService.deleteTokenAtAllDevice(eq(uuid))).thenReturn(ResponseDto.success("すべてのデヴァイスからログアウト成功"));

        // when
        ResponseDto<Void> response = passwordService.forgotPassword(request);

        // then
        assertTrue(response.isSuccess());
        assertEquals("パスワード更新成功", response.getMessage());

        verify(encodeService).encode("newPassword");
        verify(memberRepository).findMemberByEmail(eq("test@example.com"));
        verify(memberRepository).updateMember(argThat(member ->
                member.getMemberId().equals(uuid) &&
                member.getPassword().equals("encodedNewPassword")));
        verify(tokenService).deleteTokenAtAllDevice(eq(uuid));
    }

    @Test
    void changePassword() {
        // given
        String tokenId = UUID.randomUUID().toString();
        String memberId = UUID.randomUUID().toString();
        ChangePwRequestDto request = new ChangePwRequestDto();
        request.setTokenId(tokenId);
        request.setMemberId(memberId);
        request.setOldPassword("password");
        request.setNewPassword("newPassword");

        Member dbMember = Member.builder().loginId("test").password("encodedPassword").email("test@example.com").build();
        when(memberRepository.findMemberByMemberId(eq(memberId))).thenReturn(Optional.of(dbMember));
        when(encodeService.matches("password", "encodedPassword")).thenReturn(true);
        when(encodeService.encode("newPassword")).thenReturn("encodedNewPassword");
        when(memberRepository.updateMember(argThat(member ->
                        member.getMemberId().equals(memberId) &&
                        member.getPassword().equals("encodedNewPassword")))
        ).thenReturn(true);

        // when
        ResponseDto<Void> response = passwordService.changePassword(request);

        // then
        assertTrue(response.isSuccess());
        assertEquals("パスワード修正成功", response.getMessage());

        verify(memberRepository).findMemberByMemberId(eq(memberId));
        verify(encodeService).matches("password", "encodedPassword");
        verify(encodeService).encode("newPassword");
        verify(memberRepository).updateMember(argThat(member ->
                        member.getMemberId().equals(memberId) &&
                        member.getPassword().equals("encodedNewPassword")));
        verify(tokenService).deleteTokenOtherDevice(memberId, tokenId);
    }
    @Test
    @DisplayName("<changePassword>パスワード不一致からの失敗")
    void changePasswordFailsWhenPasswordIncorrect() {
        // given
        String tokenId = UUID.randomUUID().toString();
        String memberId = UUID.randomUUID().toString();
        ChangePwRequestDto request = new ChangePwRequestDto();
        request.setTokenId(tokenId);
        request.setMemberId(memberId);
        request.setOldPassword("wrongPassword");

        Member dbMember = Member.builder().loginId("test").password("encodedPassword").email("test@example.com").build();
        when(memberRepository.findMemberByMemberId(eq(memberId))).thenReturn(Optional.of(dbMember));
        when(encodeService.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        // when
        ResponseDto<Void> response = passwordService.changePassword(request);

        // then
        assertFalse(response.isSuccess());
        assertEquals("パスワード修正失敗", response.getMessage());

        verify(memberRepository).findMemberByMemberId(eq(memberId));
        verify(encodeService).matches("wrongPassword", "encodedPassword");
    }
}