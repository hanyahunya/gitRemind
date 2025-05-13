package com.hanyahunya.gitRemind.member.service;

import com.hanyahunya.gitRemind.member.dto.*;
import com.hanyahunya.gitRemind.token.dto.JwtTokenPairResponseDto;
import com.hanyahunya.gitRemind.token.service.TokenService;
import com.hanyahunya.gitRemind.util.ResponseDto;
import com.hanyahunya.gitRemind.member.entity.Member;
import com.hanyahunya.gitRemind.member.repository.MemberRepository;
import com.hanyahunya.gitRemind.util.cookieHeader.SetResultDto;
import com.hanyahunya.gitRemind.util.service.EncodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final TokenService tokenService;
    private final EncodeService encodeService;

    @Override
    public ResponseDto<Void> join(JoinRequestDto joinRequestDto) {
        UUID uuid = UUID.randomUUID();
        Member member = joinRequestDto.dtoToEntity();
        member.setMemberId(uuid.toString());
        // パスワードencode
        member.setPassword(encodeService.encode(member.getPassword()));
        boolean success = memberRepository.saveMember(member);
        if (success) {
            return ResponseDto.success("会員登録成功");
        } else {
            return ResponseDto.fail("会員登録失敗", null);
        }
    }

    @Override
    public SetResultDto login(LoginRequestDto loginRequestDto) {
        Optional<Member> optionalMember = memberRepository.findMemberByLoginId(loginRequestDto.dtoToEntity());
        if (optionalMember.isPresent()) {
            String reqPw = loginRequestDto.getPassword();
            Member member = optionalMember.get();
            if (encodeService.matches(reqPw, member.getPassword())) {
                ResponseDto<JwtTokenPairResponseDto> responseDto = tokenService.issueTokens(member.getMemberId());
                String accessToken = responseDto.getData().getAccessToken();
                String refreshToken = responseDto.getData().getRefreshToken();
                return SetResultDto.builder().success(true).accessToken(accessToken).refreshToken(refreshToken).build();
            }
        }
        return SetResultDto.builder().build();
    }

    @Override
    public ResponseDto<MemberInfoResponseDto> getInfo(String memberId) {
        return memberRepository.findMemberByMemberId(memberId)
                .map(member ->
                        ResponseDto.success("ユーザー情報ロード成功", MemberInfoResponseDto.set(
                                member.getEmail(),
                                member.getCountry()
                        ))
                )
                .orElseGet(() -> ResponseDto.fail("ユーザー情報ロード失敗"));
    }

    @Override
    @Transactional
    public SetResultDto deleteMember(DeleteMemberRequestDto requestDto) {
        Optional<Member> optionalMember = memberRepository.findMemberByMemberId(requestDto.getMemberId());
        if (optionalMember.isPresent()) {
            Member dbMember = optionalMember.get();
            if (!requestDto.getLoginId().equals(dbMember.getLoginId())) { // 향후 지울수도 있음 *왜 액세스 토큰을 줬는데 아이디까지 기억하고 있어야 하지?
                return SetResultDto.builder().build();
            }
            if (encodeService.matches(requestDto.getPassword(), dbMember.getPassword())) {
                if(memberRepository.deleteMember(requestDto.toEntity())) {
//                    ユーザーがDBから削除されたら、トリガーより繋がられたトークンも削除される。
//                    if (tokenService.deleteTokenAtAllDevice(requestDto.getMemberId()).isSuccess()) {
                    return SetResultDto.builder().success(true).deleteAccessToken(true).deleteRefreshToken(true).build();
                }
            }
        }
        return SetResultDto.builder().build();
    }

    @Override
    public ResponseDto<Void> updateMember(UpdateMemberRequestDto requestDto) {
        Optional<Member> optionalMember = memberRepository.findMemberByMemberId(requestDto.getMemberId());
        if (optionalMember.isPresent()) {
            Member dbMember = optionalMember.get();
            if (requestDto.getLoginId().equals(dbMember.getLoginId()) && encodeService.matches(requestDto.getPassword(), dbMember.getPassword())) {
                requestDto.setPassword(null);
                if (memberRepository.updateMember(requestDto.toEntity())) {
                    return ResponseDto.success("ユーザー情報更新成功");
                }
            }
        }
        return ResponseDto.fail("ユーザー情報更新失敗");
    }
}
