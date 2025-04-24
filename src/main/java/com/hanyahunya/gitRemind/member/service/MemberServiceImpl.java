package com.hanyahunya.gitRemind.member.service;

import com.hanyahunya.gitRemind.member.dto.*;
import com.hanyahunya.gitRemind.token.service.TokenService;
import com.hanyahunya.gitRemind.util.ResponseDto;
import com.hanyahunya.gitRemind.member.entity.Member;
import com.hanyahunya.gitRemind.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final TokenService tokenService;
    private final PwEncodeService pwEncodeService;

    @Override
    public ResponseDto<JwtResponseDto> join(JoinRequestDto joinRequestDto) {
        UUID uuid = UUID.randomUUID();
        Member member = joinRequestDto.dtoToEntity();
        member.setMid(uuid.toString());
        // パスワードencode
        member.setPw(pwEncodeService.encode(member.getPw()));
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
        Optional<Member> optionalMember = memberRepository.validateMember(loginRequestDto.dtoToEntity());
        if (optionalMember.isPresent()) {
            String reqPw = loginRequestDto.getPw();
            Member member = optionalMember.get();
            if (pwEncodeService.matches(reqPw, member.getPw())) {
                return ResponseDto.success("ログイン成功", JwtResponseDto.set(tokenService.generateToken(member)));
            } else {
                return ResponseDto.fail("ログイン失敗");
            }
        } else {
            return ResponseDto.fail("ログイン失敗");
        }
    }

    @Override
    public ResponseDto<MemberInfoResponseDto> getInfo(String mid) {
        return memberRepository.findMemberByMid(mid)
                .map(member ->
                        ResponseDto.success("ユーザー情報ロード成功", MemberInfoResponseDto.set(member.getEmail()))
                )
                .orElseGet(() -> ResponseDto.fail("ユーザー情報ロード失敗"));
    }

    @Override
    public ResponseDto<Void> deleteMember(DeleteMemberRequestDto requestDto) {
        requestDto.setPw(pwEncodeService.encode(requestDto.getPw()));
        if(memberRepository.deleteMember(requestDto.toEntity())) {
            return ResponseDto.success("ユーザー退会成功");
        } else {
            return ResponseDto.success("ユーザー退会失敗");
        }
    }

    @Override
    public ResponseDto<Void> updateMember(UpdateMemberRequestDto requestDto) {
        Optional<Member> optionalMember = memberRepository.validateMember(requestDto.toEntity());
        if (optionalMember.isPresent()) {
            Member dbMember = optionalMember.get();
            if (pwEncodeService.matches(requestDto.getPw(), dbMember.getPw())) {
                if (memberRepository.updateMember(requestDto.toEntity())) {
                    return ResponseDto.success("ユーザー情報更新成功");
                }
            }
        }
        return ResponseDto.fail("ユーザー情報更新失敗");
    }
}
