package com.hanyahunya.gitRemind.token.repository;

public interface MemberTokenRepository {
    boolean saveMemberToken(String memberId, String tokenId);

    String findMemberIdByTokenId(String tokenId);

    boolean deleteAllByMemberId(String memberId);

    void deleteAllByMemberIdAndTokenIdNot(String memberId, String tokenId);
}
