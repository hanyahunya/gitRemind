package com.hanyahunya.gitRemind.member.service;

import com.hanyahunya.gitRemind.member.dto.ResetPwRequestDto;
import com.hanyahunya.gitRemind.util.ResponseDto;

public interface PasswordService {
    ResponseDto<Void> forgotPassword(ResetPwRequestDto resetPwRequestDto);

    ResponseDto<Void> changePassword();
}
