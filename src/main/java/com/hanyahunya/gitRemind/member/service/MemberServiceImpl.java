package com.hanyahunya.gitRemind.member.service;

import com.hanyahunya.gitRemind.member.dto.*;
import com.hanyahunya.gitRemind.token.service.AccessTokenService;
import com.hanyahunya.gitRemind.util.ResponseDto;
import com.hanyahunya.gitRemind.member.entity.Member;
import com.hanyahunya.gitRemind.member.repository.MemberRepository;
import com.hanyahunya.gitRemind.util.service.EncodeService;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final AccessTokenService accessTokenService;
    private final EncodeService encodeService;

    @Override
    public ResponseDto<JwtResponseDto> join(JoinRequestDto joinRequestDto) {
        UUID uuid = UUID.randomUUID();
        Member member = joinRequestDto.dtoToEntity();
        member.setMemberId(uuid.toString());
        // パスワードencode
        member.setPassword(encodeService.encode(member.getPassword()));
        boolean success = memberRepository.saveMember(member);
        if (success) {
            String token = accessTokenService.generateToken(member);
            return ResponseDto.success("会員登録成功", JwtResponseDto.set(token));
        } else {
            return ResponseDto.fail("会員登録失敗", null);
        }
    }

    @Override
    public ResponseDto<JwtResponseDto> login(LoginRequestDto loginRequestDto) {
        Optional<Member> optionalMember = memberRepository.validateMember(loginRequestDto.dtoToEntity());
        if (optionalMember.isPresent()) {
            String reqPw = loginRequestDto.getPassword();
            Member member = optionalMember.get();
            if (encodeService.matches(reqPw, member.getPassword())) {
                return ResponseDto.success("ログイン成功", JwtResponseDto.set(accessTokenService.generateToken(member)));
            } else {
                return ResponseDto.fail("ログイン失敗");
            }
        } else {
            return ResponseDto.fail("ログイン失敗");
        }
    }

    @Override
    public ResponseDto<MemberInfoResponseDto> getInfo(String memberId) {
        return memberRepository.findMemberByMemberId(memberId)
                .map(member ->
                        ResponseDto.success("ユーザー情報ロード成功", MemberInfoResponseDto.set(member.getEmail()))
                )
                .orElseGet(() -> ResponseDto.fail("ユーザー情報ロード失敗"));
    }

    @Override
    public ResponseDto<Void> deleteMember(DeleteMemberRequestDto requestDto) {
        Optional<Member> optionalMember = memberRepository.validateMember(requestDto.toEntity());
        if (optionalMember.isPresent()) {
            Member dbMember = optionalMember.get();
            if (encodeService.matches(requestDto.getPassword(), dbMember.getPassword())) {
                if(memberRepository.deleteMember(requestDto.toEntity())) {
                    return ResponseDto.success("ユーザー退会成功");
                }
            }
        }
        return ResponseDto.fail("ユーザー退会失敗");
    }

    @Override
    public ResponseDto<Void> updateMember(UpdateMemberRequestDto requestDto) {
        Optional<Member> optionalMember = memberRepository.validateMember(requestDto.toEntity());
        if (optionalMember.isPresent()) {
            Member dbMember = optionalMember.get();
            if (encodeService.matches(requestDto.getPassword(), dbMember.getPassword())) {
                requestDto.setPassword(null);
                if (memberRepository.updateMember(requestDto.toEntity())) {
                    return ResponseDto.success("ユーザー情報更新成功");
                }
            }
        }
        return ResponseDto.fail("ユーザー情報更新失敗");
    }
}
