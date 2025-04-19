package com.hanyahunya.gitRemind.member.service;

import com.hanyahunya.gitRemind.member.dto.ResetPwRequestDto;
import com.hanyahunya.gitRemind.member.entity.Member;
import com.hanyahunya.gitRemind.member.repository.MemberRepository;
import com.hanyahunya.gitRemind.util.ResponseDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {
    private final MemberRepository memberRepository;
    private final PwTokenService pwTokenService;

    @Override
    public ResponseDto<Void> forgotPassword(ResetPwRequestDto resetPwRequestDto) {
        if(pwTokenService.validateToken(resetPwRequestDto.getToken())) {
            Member member = Member.builder().email(resetPwRequestDto.getEmail()).pw(resetPwRequestDto.getNewPw()).build();
            if(memberRepository.updateMember(member)) {
                return ResponseDto.success("success");
            } else {
                return ResponseDto.fail("fail");
            }
        }
        return null;
    }

    @Override
    public ResponseDto<Void> changePassword() {
        return null;
    }
}
