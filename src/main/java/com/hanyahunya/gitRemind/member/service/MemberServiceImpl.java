package com.hanyahunya.gitRemind.member.service;

import com.hanyahunya.gitRemind.ResponseDto;
import com.hanyahunya.gitRemind.member.dto.JoinMemberDto;
import com.hanyahunya.gitRemind.entity.Member;
import com.hanyahunya.gitRemind.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final TokenService tokenService;

    @Override
    public ResponseDto<String> join(JoinMemberDto joinMemberDto) {
        UUID uuid = UUID.randomUUID();
        Member member = joinMemberDto.dtoToEntity();
        member.setMid(uuid.toString());
        boolean success = memberRepository.saveMember(member);
        if (success) {
            String token = tokenService.generateToken(member);
            return ResponseDto.success("会員登録成功", token);
        } else {
            return ResponseDto.fail("会員登録失敗", null);
        }
    }
}
