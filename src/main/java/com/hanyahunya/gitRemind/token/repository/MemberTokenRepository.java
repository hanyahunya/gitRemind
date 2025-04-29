package com.hanyahunya.gitRemind.token.repository;

import com.hanyahunya.gitRemind.token.entity.MemberToken;

public interface MemberTokenRepository {
    boolean saveMemberToken(String memberId, String tokenId);

    String findMemberIdByTokenId(String tokenId);

    boolean deleteMemberTokenByMemberId(String memberId);
}
