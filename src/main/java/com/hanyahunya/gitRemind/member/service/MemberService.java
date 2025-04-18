package com.hanyahunya.gitRemind.member.service;

import com.hanyahunya.gitRemind.util.ResponseDto;
import com.hanyahunya.gitRemind.member.dto.JoinRequestDto;
import com.hanyahunya.gitRemind.member.dto.JwtResponseDto;
import com.hanyahunya.gitRemind.member.dto.LoginRequestDto;
import com.hanyahunya.gitRemind.member.dto.MemberInfoResponseDto;

public interface MemberService {
    /**
     * @param joinRequestDto id, pw, email, git_addr必須
     * @return JwtResponseDto.token(String)
     */
    ResponseDto<JwtResponseDto> join(JoinRequestDto joinRequestDto);
    // todo(04.14) - add reset password / delete member / update memberInfo

    /**
     * @param loginRequestDto id,pw必須
     * @return JwtResponseDto.token(String)
     */
    ResponseDto<JwtResponseDto> login(LoginRequestDto loginRequestDto);

    /**
     * @param mid in UserPrincipal.mid
     * @return MemberInfoResponseDto.email, .git_addr
     */
    ResponseDto<MemberInfoResponseDto> getInfo(String mid);

    ResponseDto<Void> deleteMember();

    ResponseDto<Void> updateMember();
}
