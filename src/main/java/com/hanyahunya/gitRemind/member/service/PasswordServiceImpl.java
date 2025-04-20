package com.hanyahunya.gitRemind.member.service;

import com.hanyahunya.gitRemind.member.dto.ResetPwRequestDto;
import com.hanyahunya.gitRemind.member.entity.Member;
import com.hanyahunya.gitRemind.member.repository.MemberRepository;
import com.hanyahunya.gitRemind.util.ResponseDto;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {
    private final MemberRepository memberRepository;
    private final PwEncodeService pwEncodeService;

    @Override
    public ResponseDto<Void> forgotPassword(ResetPwRequestDto resetPwRequestDto) {
        resetPwRequestDto.setNewPw(pwEncodeService.encode(resetPwRequestDto.getNewPw()));
        Optional<Member> optionalMember = memberRepository.findMemberByEmail(resetPwRequestDto.getEmail());
        if(optionalMember.isPresent()) {
            Member member = Member.builder()
                    .mid(optionalMember.get().getMid())
                    .pw(resetPwRequestDto.getNewPw())
                    .build();
            if(memberRepository.updateMember(member)) {
                return ResponseDto.success("パスワード更新成功");
            } else {
                return ResponseDto.fail("パスワード更新失敗");
            }
        } else {
            return ResponseDto.fail("パスワード更新失敗");
        }
    }

    @Override
    public ResponseDto<Void> changePassword() {
        return null;
    }
}
