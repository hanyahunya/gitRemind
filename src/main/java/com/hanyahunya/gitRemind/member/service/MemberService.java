package com.hanyahunya.gitRemind.member.service;

import com.hanyahunya.gitRemind.member.dto.*;
import com.hanyahunya.gitRemind.util.ResponseDto;

public interface MemberService {
    /**
     * @param joinRequestDto loginId, password, email, git_addr必須
     * @return JwtResponseDto.token(String)
     */
    ResponseDto<JwtResponseDto> join(JoinRequestDto joinRequestDto);
    // todo(04.21) delete member / update memberInfo

    /**
     * @param loginRequestDto loginId,pw必須
     * @return JwtResponseDto.token(String)
     */
    ResponseDto<JwtResponseDto> login(LoginRequestDto loginRequestDto);

    /**
     * @param mid in UserPrincipal.memberId
     * @return MemberInfoResponseDto.email, .git_addr
     */
    ResponseDto<MemberInfoResponseDto> getInfo(String mid);

    ResponseDto<Void> deleteMember(DeleteMemberRequestDto deleteMemberRequestDto);

    ResponseDto<Void> updateMember(UpdateMemberRequestDto updateMemberRequestDto);
}
