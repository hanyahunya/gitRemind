package com.hanyahunya.gitRemind.member.service;

import com.hanyahunya.gitRemind.token.service.TokenPurpose;
import com.hanyahunya.gitRemind.util.ResponseDto;
import com.hanyahunya.gitRemind.member.dto.EmailRequestDto;
import com.hanyahunya.gitRemind.member.dto.ValidateCodeRequestDto;
import com.hanyahunya.gitRemind.util.cookieHeader.SetResultDto;

public interface AuthCodeService {
    /**
     * emailを基づいてメールを送る
     */
    ResponseDto<Void> sendAuthCode(EmailRequestDto emailRequestDto, TokenPurpose tokenPurpose);

    /**
     * メールアドレスと認証コードを基づいて認証結果をreturn
     * @param validateCodeRequestDto email, authCode必須
     */
    SetResultDto validateAuthCode(ValidateCodeRequestDto validateCodeRequestDto, TokenPurpose tokenPurpose);
}
