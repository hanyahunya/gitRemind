package com.hanyahunya.gitRemind.member.service;

import com.hanyahunya.gitRemind.member.dto.ChangePwRequestDto;
import com.hanyahunya.gitRemind.member.dto.ResetPwRequestDto;
import com.hanyahunya.gitRemind.member.entity.Member;
import com.hanyahunya.gitRemind.member.repository.MemberRepository;
import com.hanyahunya.gitRemind.token.service.TokenService;
import com.hanyahunya.gitRemind.util.ResponseDto;
import com.hanyahunya.gitRemind.util.service.EncodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {
    private final MemberRepository memberRepository;
    private final EncodeService encodeService;
    private final TokenService tokenService;

    @Override
    @Transactional
    public ResponseDto<Void> forgotPassword(ResetPwRequestDto resetPwRequestDto) {
        resetPwRequestDto.setNewPassword(encodeService.encode(resetPwRequestDto.getNewPassword()));
        Optional<Member> optionalMember = memberRepository.findMemberByEmail(resetPwRequestDto.getEmail());
        if(optionalMember.isPresent()) {
            Member dbMember = optionalMember.get();
            Member member = Member.builder()
                    .memberId(dbMember.getMemberId())
                    .password(resetPwRequestDto.getNewPassword())
                    .build();
            if(memberRepository.updateMember(member)) {
                if (tokenService.deleteTokenAtAllDevice(member.getMemberId()).isSuccess()) {
                    return ResponseDto.success("パスワード更新成功");
                } else {
                    throw new RuntimeException("TokenService.deleteTokenAtAllDevice failed");
                }
            }
        }
        return ResponseDto.fail("パスワード更新失敗");
    }

    @Override
    public ResponseDto<Void> changePassword(ChangePwRequestDto requestDto) {
        Optional<Member> optionalMember = memberRepository.findMemberByMemberId(requestDto.getMemberId());
        if(optionalMember.isPresent()) {
            Member dbMember = optionalMember.get();
            if(encodeService.matches(requestDto.getOldPassword(), dbMember.getPassword())) {
                Member member = Member.builder()
                        .memberId(requestDto.getMemberId())
                        .password(encodeService.encode(requestDto.getNewPassword()))
                        .build();
                if(memberRepository.updateMember(member)) {
                    if (tokenService.deleteTokenAtAllDevice(member.getMemberId()).isSuccess()) {
                        return ResponseDto.success("パスワード修正成功");
                    } else {
                        throw new RuntimeException("TokenService.deleteTokenAtAllDevice failed");
                    }
                }
            }
        }
        return ResponseDto.fail("パスワード修正失敗");
    }
}
