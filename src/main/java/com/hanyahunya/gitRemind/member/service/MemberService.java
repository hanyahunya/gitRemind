package com.hanyahunya.gitRemind.member.service;

import com.hanyahunya.gitRemind.member.dto.*;
import com.hanyahunya.gitRemind.util.ResponseDto;
import com.hanyahunya.gitRemind.util.cookieHeader.SetResultDto;

public interface MemberService {
    /**
     * @param joinRequestDto loginId, password, email, git_addr必須
     * @return JwtResponseDto.token(String)
     */
    ResponseDto<Void> join(JoinRequestDto joinRequestDto);

    /**
     * @param loginRequestDto loginId,pw必須
     * @return JwtResponseDto.token(String)
     */
    SetResultDto login(LoginRequestDto loginRequestDto);

    /**
     * @param mid in UserPrincipal.memberId
     * @return MemberInfoResponseDto.email, .git_addr
     */
    ResponseDto<MemberInfoResponseDto> getInfo(String mid);

    SetResultDto deleteMember(DeleteMemberRequestDto deleteMemberRequestDto);

    ResponseDto<Void> updateMember(UpdateMemberRequestDto updateMemberRequestDto);
}
