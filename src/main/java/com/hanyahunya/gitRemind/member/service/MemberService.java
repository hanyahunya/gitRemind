package com.hanyahunya.gitRemind.member.service;

import com.hanyahunya.gitRemind.ResponseDto;
import com.hanyahunya.gitRemind.member.dto.JoinMemberDto;

public interface MemberService {
    /**
     *
     * @param joinMemberDto
     * @return 成功結果,jwt todo(04.14) return jwt(mid)
     */
    ResponseDto<String> join(JoinMemberDto joinMemberDto);
    // todo(04.14) - add reset password / delete member / update memberInfo / get memberInfo
}
