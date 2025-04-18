package com.hanyahunya.gitRemind.member.service;

import com.hanyahunya.gitRemind.util.ResponseDto;

public interface PasswordService {
    ResponseDto<Void> forgotPassword();

    ResponseDto<Void> changePassword();
}
