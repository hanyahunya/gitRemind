package com.hanyahunya.gitRemind.member.service;

import com.hanyahunya.gitRemind.util.ResponseDto;
import com.hanyahunya.gitRemind.member.dto.JoinRequestDto;
import com.hanyahunya.gitRemind.member.dto.JwtResponseDto;
import com.hanyahunya.gitRemind.member.dto.LoginRequestDto;
import com.hanyahunya.gitRemind.member.dto.MemberInfoResponseDto;
import com.hanyahunya.gitRemind.member.entity.Member;
import com.hanyahunya.gitRemind.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final TokenService tokenService;

    @Override
    public ResponseDto<JwtResponseDto> join(JoinRequestDto joinRequestDto) {
        UUID uuid = UUID.randomUUID();
        Member member = joinRequestDto.dtoToEntity();
        member.setMid(uuid.toString());
        // todo encodePw(04.18)
        boolean success = memberRepository.saveMember(member);
        if (success) {
            String token = tokenService.generateToken(member);
            return ResponseDto.success("会員登録成功", JwtResponseDto.set(token));
        } else {
            return ResponseDto.fail("会員登録失敗", null);
        }
    }

    @Override
    public ResponseDto<JwtResponseDto> login(LoginRequestDto loginRequestDto) {
        // todo encodePw(04.18)
//        loginRequestDto.getPw();
//        loginRequestDto.setPw("");
        return memberRepository.validateMember(loginRequestDto.dtoToEntity())
                .map(member ->
                        ResponseDto.success("ログイン成功", JwtResponseDto.set(tokenService.generateToken(member)))
                )
                .orElseGet(() -> ResponseDto.fail("ログイン失敗"));
    }

    @Override
    public ResponseDto<MemberInfoResponseDto> getInfo(String mid) {
        return memberRepository.findMemberByMid(mid)
                .map(member ->
                        ResponseDto.success("ユーザー情報ロード成功", MemberInfoResponseDto.set(member.getEmail(), member.getGit_username()))
                )
                .orElseGet(() -> ResponseDto.fail("ユーザー情報ロード失敗"));
    }

    @Override
    public ResponseDto<Void> deleteMember() {
        return null;
    }

    @Override
    public ResponseDto<Void> updateMember() {
        return null;
    }
}
