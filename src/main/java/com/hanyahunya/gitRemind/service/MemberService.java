package com.hanyahunya.gitRemind.service;

import com.hanyahunya.gitRemind.dto.JoinMemberDto;

public interface MemberService {
    /**
     *
     * @param joinMemberDto
     * @return 成功結果 todo(04.14) return jwt(mid)
     */
    boolean join(JoinMemberDto joinMemberDto);
    // todo(04.14) - add reset password / delete member / update memberInfo / get memberInfo
}
