package com.hanyahunya.gitRemind.member.repository;

import com.hanyahunya.gitRemind.member.entity.Member;

import java.util.Optional;

public interface MemberRepository {
    boolean saveMember(Member member);

    Optional<Member> findMemberByMid(String mid);
    Optional<Member> findMemberByEmail(String email);

    Optional<Member> validateMember(Member member);

    boolean updateMember(Member member);

    boolean deleteMember(Member member);
}
