package com.hanyahunya.gitRemind.service;

import com.hanyahunya.gitRemind.dto.JoinMemberDto;
import com.hanyahunya.gitRemind.entity.Member;
import com.hanyahunya.gitRemind.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;

    @Override
    public boolean join(JoinMemberDto joinMemberDto) {
        UUID uuid = UUID.randomUUID();
        Member member = joinMemberDto.dtoToEntity();
        member.setMid(uuid.toString());
        return memberRepository.saveMember(member);
    }
}
