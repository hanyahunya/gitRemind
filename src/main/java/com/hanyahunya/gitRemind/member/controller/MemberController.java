package com.hanyahunya.gitRemind.member.controller;

import com.hanyahunya.gitRemind.ResponseDto;
import com.hanyahunya.gitRemind.member.dto.JoinMemberDto;
import com.hanyahunya.gitRemind.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/join")
    public ResponseEntity<ResponseDto<String>> join(@RequestBody JoinMemberDto joinMemberDto) {
        ResponseDto<String> responseDto = memberService.join(joinMemberDto);
        if (responseDto.isSuccess()) {
            return ResponseEntity.ok().body(responseDto);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
