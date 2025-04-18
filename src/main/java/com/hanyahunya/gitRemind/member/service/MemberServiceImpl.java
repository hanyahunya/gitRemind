package com.hanyahunya.gitRemind.member.service;

import com.hanyahunya.gitRemind.util.ResponseDto;
import com.hanyahunya.gitRemind.member.dto.JoinRequestDto;
import com.hanyahunya.gitRemind.member.dto.JwtResponseDto;
import com.hanyahunya.gitRemind.member.dto.LoginRequestDto;
import com.hanyahunya.gitRemind.member.dto.MemberInfoResponseDto;
import com.hanyahunya.gitRemind.member.entity.Member;
import com.hanyahunya.gitRemind.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public ResponseDto<JwtResponseDto> join(JoinRequestDto joinRequestDto) {
        UUID uuid = UUID.randomUUID();
        Member member = joinRequestDto.dtoToEntity();
        member.setMid(uuid.toString());
        // パスワードencode
        member.setPw(passwordEncoder.encode(member.getPw()));
        boolean success = memberRepository.saveMember(member);
        if (success) {
            String token = tokenService.generateToken(member);
            return ResponseDto.success("会員登録成功", JwtResponseDto.set(token));
        } else {
            return ResponseDto.fail("会員登録失敗", null);
        }
    }

    @Override
    public ResponseDto<JwtResponseDto> login(LoginRequestDto loginRequestDto) {
        Optional<Member> optionalMember = memberRepository.validateMember(loginRequestDto.dtoToEntity());
        if (optionalMember.isPresent()) {
            String reqPw = loginRequestDto.getPw();
            Member member = optionalMember.get();
            if (passwordEncoder.matches(reqPw, member.getPw())) {
                return ResponseDto.success("ログイン成功", JwtResponseDto.set(tokenService.generateToken(member)));
            } else {
                return ResponseDto.fail("ログイン失敗");
            }
        } else {
            return ResponseDto.fail("ログイン失敗");
        }
    }

    @Override
    public ResponseDto<MemberInfoResponseDto> getInfo(String mid) {
        return memberRepository.findMemberByMid(mid)
                .map(member ->
                        ResponseDto.success("ユーザー情報ロード成功", MemberInfoResponseDto.set(member.getEmail(), member.getGit_username()))
                )
                .orElseGet(() -> ResponseDto.fail("ユーザー情報ロード失敗"));
    }

    @Override
    public ResponseDto<Void> deleteMember() {
        return null;
    }

    @Override
    public ResponseDto<Void> updateMember() {
        return null;
    }
}
